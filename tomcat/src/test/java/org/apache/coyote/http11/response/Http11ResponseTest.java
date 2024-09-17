package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Http11ResponseTest {

    @Test
    @DisplayName("status: 200 OK인 response를 생성할 수 있다.")
    void ok() {
        Http11Response response = Http11Response.ok();

        assertThat(new String(response.getBytes())).contains("HTTP/1.1 200 OK");
    }

    @Test
    @DisplayName("statusCode를 변경할 수 있다.")
    void setStatusCode() {
        Http11Response response = Http11Response.ok();
        response.setStatusCode(HttpStatusCode.BAD_REQUEST);

        assertThat(new String(response.getBytes())).contains("HTTP/1.1 400 Bad Request");
    }

    @Test
    @DisplayName("responseBody를 변경할 수 있다.")
    void setResponseBody() {
        Http11Response response = Http11Response.ok();
        response.setResponseBody("a: b");

        assertThat(new String(response.getBytes())).contains("\r\n\r\na: b");
    }

    @Test
    @DisplayName("쿠키를 변경할 수 있다.")
    void setCookie() {
        Http11Response response = Http11Response.ok();
        response.setCookie("name", "value");

        assertThat(new String(response.getBytes())).contains("Set-Cookie: name=value");
    }

    @Test
    @DisplayName("헤더를 추가할 수 있다.")
    void addHeader() {
        Http11Response response = Http11Response.ok();
        response.addHeader("key1", "value1");
        response.addHeader("key2", "value2");

        assertThat(new String(response.getBytes())).contains("key1: value1 \r\nkey2: value2 ");
    }
}
