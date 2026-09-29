package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void unsupportedMethodThrowsException() throws Exception {
        HttpRequest request = HttpRequest.from(new BufferedReader(
                new StringReader("PUT / HTTP/1.1\r\n\r\n")
        ));
        HttpResponse response = new HttpResponse();

        assertThatThrownBy(() -> new AbstractController() {}.service(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported HTTP method: PUT");

        assertThat(response.toString()).startsWith("HTTP/1.1 200 OK");
    }
}
