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

    @RestController
    static class EchoController {
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
