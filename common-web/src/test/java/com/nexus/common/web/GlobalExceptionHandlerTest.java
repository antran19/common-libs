package com.nexus.common.web;

import com.nexus.common.core.ApiResponse;
import com.nexus.common.core.exception.ConflictException;
import com.nexus.common.core.exception.NotFoundException;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundException_mapsTo404() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleNotFound(new NotFoundException("USER_NOT_FOUND", "User not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getError().getCode()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void conflictException_mapsTo409() {
        ResponseEntity<ApiResponse<Object>> response =
                handler.handleConflict(new ConflictException("EMAIL_TAKEN", "Email already registered"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError().getCode()).isEqualTo("EMAIL_TAKEN");
    }

    @Test
    void errorResponseException_mapsToItsOwnStatus_notInternalServerError() {
        HttpMediaTypeNotSupportedException ex =
                new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN, List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<ApiResponse<Object>> response = handler.handleErrorResponse(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError().getCode()).isEqualTo("REQUEST_ERROR");
    }

    @Test
    void unexpectedException_stillMapsTo500() {
        ResponseEntity<ApiResponse<Object>> response = handler.handleUnexpected(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError().getCode()).isEqualTo("INTERNAL_ERROR");
    }

    enum Kind { A, B }

    record Item(int quantity) {
    }

    record OrderDto(java.math.BigDecimal amount, java.time.Instant at, Kind kind, boolean paid, java.util.List<Item> items) {
    }

    @RestController
    static class EchoController {
        @PostMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
        public String order(@RequestBody OrderDto body) {
            return "ok";
        }

        @org.springframework.web.bind.annotation.GetMapping("/page")
        public String page(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page) {
            return "ok" + page;
        }

        @PostMapping(path = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
        public String echo(@Validated @RequestBody EchoDto body) {
            return body.name();
        }
    }

    record EchoDto(@NotBlank String name) {
    }

    /**
     * End-to-end dispatch tests proving Spring's own resolution picks the correct
     * @ExceptionHandler for framework-thrown exceptions, and that the more specific
     * MethodArgumentNotValidException handler still wins over the broader ErrorResponse
     * interface handler.
     */
    @Nested
    class DispatchTests {

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(new EchoController())
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();
        }

        @Test
        void wrongHttpMethod_mapsTo405ViaErrorResponseHandler_notInternalServerError() throws Exception {
            mockMvc.perform(get("/echo"))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("REQUEST_ERROR"));
        }

        @Test
        void wrongContentType_mapsTo415ViaErrorResponseHandler_notInternalServerError() throws Exception {
            mockMvc.perform(post("/echo").contentType(MediaType.TEXT_PLAIN).content("hi"))
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("REQUEST_ERROR"));
        }

        private org.springframework.test.web.servlet.ResultActions postOrder(String json) throws Exception {
            return mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(json));
        }

        @Test
        void brokenJson_is400MalformedRequest_notInternalServerError() throws Exception {
            postOrder("{\"amount\": 5,").andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
            postOrder("not json at all").andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
        }

        @Test
        void anEmptyBody_is400MalformedRequest() throws Exception {
            mockMvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
        }

        @Test
        void aTextWhereANumberIsExpected_is400ValidationErrorOnThatField() throws Exception {
            postOrder("{\"amount\": \"abc\"}").andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].field").value("amount"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].message").value("must be a number"));
        }

        @Test
        void aBadDate_is400ValidationErrorOnThatField() throws Exception {
            postOrder("{\"at\": \"yesterday\"}").andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.fieldErrors[0].field").value("at"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].message").value("must be a valid ISO-8601 date or time"));
        }

        @Test
        void anUnknownEnumValue_listsTheAllowedOnes() throws Exception {
            postOrder("{\"kind\": \"Z\"}").andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.fieldErrors[0].field").value("kind"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].message").value("must be one of [A, B]"));
        }

        @Test
        void aWrongTypeInsideAList_namesTheElementPath() throws Exception {
            postOrder("{\"items\": [{\"quantity\": 1}, {\"quantity\": \"many\"}]}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.fieldErrors[0].field").value("items[1].quantity"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].message").value("must be a number"));
        }

        @Test
        void aStructureOfTheWrongShape_is400InvalidType() throws Exception {
            postOrder("{\"items\": \"not a list\"}").andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].field").value("items"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].message").value("has an invalid type"));
        }

        @Test
        void theAnswerNeverEchoesJacksonInternals() throws Exception {
            String body = postOrder("{\"amount\": \"abc\"}").andReturn().getResponse().getContentAsString();
            assertThat(body).doesNotContain("com.fasterxml").doesNotContain("java.math").doesNotContain("Cannot deserialize");
        }

        @Test
        void aValidBody_stillWorks() throws Exception {
            postOrder("{\"amount\": 5, \"at\": \"2026-11-09T00:00:00Z\", \"kind\": \"A\", \"paid\": true}")
                    .andExpect(status().isOk());
        }

        @Test
        void aQueryValueOfTheWrongType_is400ValidationErrorNamingTheParameter() throws Exception {
            mockMvc.perform(get("/page").param("page", "abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].field").value("page"))
                    .andExpect(jsonPath("$.error.fieldErrors[0].message").value("must be a number"));
            mockMvc.perform(get("/page").param("page", "2")).andExpect(status().isOk());
        }

        @Test
        void beanValidationFailure_stillHandledByMoreSpecificHandler_notErrorResponseHandler() throws Exception {
            mockMvc.perform(post("/echo").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
        }
    }

    // Sanity check (outside MockMvc) that this exception really does implement ErrorResponse,
    // which is what makes the dispatch tests above meaningful.
    @Test
    void httpRequestMethodNotSupportedException_isAnErrorResponse() {
        assertThat(new HttpRequestMethodNotSupportedException("GET"))
                .isInstanceOf(org.springframework.web.ErrorResponse.class);
    }
}
