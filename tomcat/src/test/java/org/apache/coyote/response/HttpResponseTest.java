package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void HTTP_상태를_설정한다() {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        httpResponse.setStatus(HttpStatus.OK);

        assertThat(httpResponse.toString()).contains("200 OK");
    }

    @Test
    void CONTENT_TYPE을_설정한다() {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        httpResponse.setContentType(ContentType.APPLICATION_JSON.getType());

        assertThat(httpResponse.toString()).contains("Content-Type: application/json");
    }

    @Test
    void LOCATION을_설정한다() {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        httpResponse.setLocation("/index.html");

        assertThat(httpResponse.toString()).contains("Location: /index.html");
    }

    @Test
    void 쿠키를_추가한다() {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        httpResponse.addCookie("JSESSIONID", String.valueOf(123));

        assertThat(httpResponse.toString()).contains("Set-Cookie: JSESSIONID=123");
    }

    @Test
    void CONTENT를_설정한다() {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        httpResponse.setContent("Hello world!");

        assertThat(httpResponse.toString()).contains("Content-Length: 12", "Hello world!");
    }

    @Test
    void HTTP_응답_메시지로_변환한다() {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.APPLICATION_JSON.getType());
        httpResponse.addCookie("JSESSIONID", String.valueOf(123));
        httpResponse.setContent("Hello world!");

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: application/json ",
                "Set-Cookie: JSESSIONID=123 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
        assertThat(httpResponse).hasToString(expected);
    }
}
