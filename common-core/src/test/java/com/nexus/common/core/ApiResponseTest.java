package com.nexus.common.core;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void ok_wrapsDataAndMarksSuccess() {
        ApiResponse<String> response = ApiResponse.ok("hello");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("hello");
        assertThat(response.getError()).isNull();
    }

    @Test
    void error_carriesErrorAndNoData() {
        ApiError error = new ApiError("USER_NOT_FOUND", "User not found", List.of());
        ApiResponse<Object> response = ApiResponse.error(error);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getError().getCode()).isEqualTo("USER_NOT_FOUND");
    }
}
