package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;
import support.HttpFactory;

class HomeControllerTest {

    private static final Controller CONTROLLER = new HomeController();

    @Test
    void handle() {
        String httpRequest = HttpFactory.get("/index.html");

        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);
        HttpResponse httpResponse = HttpFactory.create();
        CONTROLLER.service(HttpRequest.from(bufferedReader), httpResponse);

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Content-Type")).contains(
                        ContentType.TEXT_PLAIN.getValue()),
                () -> assertThat(httpResponse.getMessageBody().getValue()).isEqualTo("Hello world!")
        );
    }
}
