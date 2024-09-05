package com.techcourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RequestLineTest {

    @Test
    @DisplayName("")
    void validateSize() {
        String test = "GET /index.html HTTP/1.1 wrong";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid request line");
    }

    @Test
    @DisplayName("")
    void validateMethod1() {
        String test = "GETT /index.html HTTP/1.1";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method GETT is not supported");
    }

    @Test
    @DisplayName("")
    void validateMethod2() {
        String test = " /index.html HTTP/1.1";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Method cannot be null or empty");
    }

    @Test
    @DisplayName("")
    void validatePath1() {
        String test = "GET  HTTP/1.1";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path cannot be null or empty");
    }

    @Test
    @DisplayName("")
    void validatePath2() {
        String test = "GET index.html HTTP/1.1";

        assertThatThrownBy(() -> RequestLine.of(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path must start with '/'");
    }

}
