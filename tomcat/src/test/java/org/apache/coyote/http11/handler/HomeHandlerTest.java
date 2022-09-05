package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class HomeHandlerTest {

    @Test
    void handle() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Handler handler = new HomeHandler();
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(httpRequest);

        HttpResponse httpResponse = handler.handle(HttpRequest.from(bufferedReader));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(httpResponse.getHeaders().getValue().get("Content-Type")).contains(ContentType.TEXT_HTML.getValue()),
                () -> assertThat(httpResponse.getMessageBody().getValue()).isEqualTo("Hello world!")
        );
    }
}
