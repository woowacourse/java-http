package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("HttpResponse를 올바른 HTTTP 응답 메시지로 변환한다.")
    @Test
    void toHttpMessage() {
        HttpResponse response = HttpResponse.ok()
                .setStatus(HttpStatusCode.OK)
                .setContentType("text/html")
                .addHeader("XXX-Custom-Header", "hello")
                .setBody("Hello, World!");

        String httpMessage = response.toHttpMessage();

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "XXX-Custom-Header: hello",
                "Content-Length: 13",
                "Content-Type: text/html",
                "",
                "Hello, World!");
        assertThat(httpMessage).isEqualTo(expected);
    }
}
