package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestBuilder;

class RootControllerTest {

    private OutputStream outputStream;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        response = new HttpResponse(outputStream);
    }

    @AfterEach
    void cleanUp() {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("GET / 요청 시, Hello world를 응답한다.")
    @Test
    void getRootUrl() {
        // given
        HttpRequest request = HttpRequestBuilder.builder()
                .setRequest("GET", "/")
                .build();
        RootController controller = new RootController();
        String expected = "Hello world!";

        // when
        controller.doGet(request, response);
        String actual = outputStream.toString();

        // then
        assertThat(actual).contains(expected);
    }
}