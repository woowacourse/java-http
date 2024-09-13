package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("리다이렉트 헤더를 설정한다.")
    @Test
    void redirectTo() {
        String location = "/index.html";
        HttpResponse response = new HttpResponse();

        response.redirectTo(location);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.FOUND,
                Map.of(HttpHeader.LOCATION.getName(), "/index.html")
        );
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("응답 쿠키를 추가한다.")
    @Test
    void addCookie() {
        HttpCookie cookie = new HttpCookie("name", "value");
        HttpResponse response = new HttpResponse(HttpStatus.OK);

        response.addCookie(cookie);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.OK,
                Map.of(HttpHeader.SET_COOKIE.getName(), "name=value")
        );
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("plain 텍스트 본문을 추가한다.")
    @Test
    void setTextBody() {
        String body = "body";
        int contentLength = body.getBytes().length;
        HttpResponse response = new HttpResponse(HttpStatus.OK);

        response.setTextBody(body);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.OK,
                Map.of(HttpHeader.CONTENT_TYPE.getName(), "text/plain;charset=utf-8 ",
                        HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(contentLength)),
                "body"
        );
        assertThat(response).isEqualTo(expectedResponse);
    }
}
