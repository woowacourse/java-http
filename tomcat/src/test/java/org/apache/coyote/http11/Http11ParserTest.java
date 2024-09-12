package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.Test;

public class Http11ParserTest {

    @Test
    void HTTP_응답을_직렬화할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(".html", "Hello world!");

        // when
        String actualResponse = Http11Parser.writeHttpResponse(response);

        // then
        final String expectedResponse = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/html;charset=utf-8 ",
                "",
                "Hello world!"
        );

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void 본문이_없는_HTTP_응답을_직렬화할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation("/index.html");

        // when
        String actualResponse = Http11Parser.writeHttpResponse(response);

        // then
        final String expectedResponse = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Length: 0 ",
                "Location: /index.html ",
                ""
        );

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
