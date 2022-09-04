package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void ok() {
        HttpResponse httpResponse = HttpResponse.ok(ContentType.TEXT_HTML, new MessageBody("Message Body"));

        assertAll(
                () -> assertThat(httpResponse.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(httpResponse.getHeaders().getHeaders()).isEqualTo(String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 12 ")),
                () -> assertThat(httpResponse.getMessageBody().getValue()).isEqualTo("Message Body")
        );
    }
}
