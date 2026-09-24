package org.apache.coyote.http11.response;

import org.apache.coyote.http11.StaticResource;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpResponseTest {
    @Test
    void toBytes() {
        // given
        final HttpResponse response = new HttpResponse();
        response.setBody(HttpStatus.OK, "text/html", "Hello world!");

        // when
        final String actual = new String(response.toBytes(), StandardCharsets.UTF_8);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void contentLengthIsByteLength() {
        // given
        final HttpResponse response = new HttpResponse();
        response.setBody(HttpStatus.BAD_REQUEST, "text/plain", "잘못된 요청");

        // when
        final String actual = new String(response.toBytes(), StandardCharsets.UTF_8);

        // then
        assertThat(actual)
                .startsWith("HTTP/1.1 400 Bad Request ")
                .contains("Content-Length: 16 ");
    }

    @Test
    void fromStaticResource() {
        // given
        final StaticResource staticResource = new StaticResource("body { }", "text/css");

        // when
        final HttpResponse response = new HttpResponse();
        response.setStaticResource(HttpStatus.OK, staticResource);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: 8 ",
                "",
                "body { }");
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void addCookie() {
        // given
        final HttpResponse response = new HttpResponse();
        response.setBody(HttpStatus.OK, "text/html", "Hello world!");

        // when
        response.addCookie("JSESSIONID", "abc");

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Set-Cookie: JSESSIONID=abc ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void eachCookieIsWrittenAsSeparateSetCookieHeader() {
        // given
        final HttpResponse response = new HttpResponse();
        response.setBody(HttpStatus.OK, "text/html", "");

        // when
        response.addCookie("JSESSIONID", "abc");
        response.addCookie("theme", "dark");

        // then
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8))
                .contains("\r\nSet-Cookie: JSESSIONID=abc \r\nSet-Cookie: theme=dark \r\n");
    }

    @Test
    void sameNameCookieIsReplaced() {
        // given
        final HttpResponse response = new HttpResponse();
        response.setBody(HttpStatus.OK, "text/html", "");

        // when
        response.addCookie("JSESSIONID", "old");
        response.addCookie("JSESSIONID", "new");

        // then
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8))
                .contains("Set-Cookie: JSESSIONID=new ")
                .doesNotContain("JSESSIONID=old");
    }

    @Test
    void responseMustBeConfiguredBeforeSerialization() {
        final HttpResponse response = new HttpResponse();
        response.addCookie("JSESSIONID", "abc");

        assertThatThrownBy(response::toBytes)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("HTTP 응답이 설정되지 않았습니다.");
    }
}
