package org.apache.coyote.http11.response;

import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.request.Cookie;
import org.apache.coyote.http11.request.Request;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.EntityHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.response.StatusCode.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ResponseTest {

    @Test
    void 리다이렉트_응답을_생성한다() {
        //given
        final String path = "/abc";
        final Response redirectResponse = new Response();

        //when
        redirectResponse.redirect(path);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(redirectResponse.getStatusLine().getStatusCode()).isEqualTo(StatusCode.FOUND);
            softAssertions.assertThat(redirectResponse.getHeaders().getValue(ResponseHeader.LOCATION)).isEqualTo(path);
        });
    }

    @Test
    void String으로_반환한다() {
        //given
        final Headers headers = new Headers();
        final String cookie = "a=b";
        final String body = "12345";
        final Response response = new Response();
        response.setStatusCode(StatusCode.OK);
        response.addHeader(ResponseHeader.SET_COOKIE, cookie);
        response.writeBody(body);

        //when
        final String actual = response.parseString();

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).contains("HTTP/1.1 200 OK");
            softAssertions.assertThat(actual).contains("Set-Cookie: " + cookie);
            softAssertions.assertThat(actual).contains(body);
        });
    }

    @Test
    void 바디에_String을_입력한다_그_때_컨텐트_길이_헤더도_결정된다() {
        //given
        final Response response = new Response();

        //when
        String content = "1234";
        response.writeBody(content);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getHeaders().getValue(CONTENT_LENGTH)).isEqualTo(String.valueOf(content.length()));
            softAssertions.assertThat(response.getBody()).contains(content);
        });
    }

    @Test
    void 정적_페이지를_응답하다_그_때_컨텐트_길이_헤더도_결정된다() {
        //given
        final Response response = new Response();

        //when
        response.writeStaticResource("/index.html");

        //then
        final String actual = response.parseString();
        assertThat(actual).contains("Content-Length: 5564");
    }

    @Test
    void 헤더를_추가한다() {
        //given
        final Response response = new Response();

        //when
        response.addHeader(CONTENT_TYPE, "application/json");

        //then
        final String actual = response.parseString();
        assertThat(actual).contains("Content-Type: application/json");
    }

    @Test
    void 쿠키를_추가한다() {
        //given
        final Cookie cookie = new Cookie("a", "1");
        final Response response = new Response();

        //when
        response.addCookie(cookie);

        //then
        final String actual = response.parseString();
        assertThat(actual).contains("Set-Cookie: a=1");
    }

    @Test
    void 응답_코드를_세팅한다() {
        //given
        final Response response = new Response();

        //when
        response.setStatusCode(UNAUTHORIZED);

        //then
        final String actual = response.parseString();
        assertThat(actual).contains("401 UNAUTHORIZED");
    }

    @Test
    void Request를_보고_ContenType을_결정하는_전처리를_한다() throws IOException {
        //given
        final String requestString = String.join("\r\n",
                "GET /index.html?a=1&b=2 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final Request request = Request.from(bufferedReader);
        final Response response = new Response();

        //when
        response.preprocess(request);

        //then
        final String actual = response.parseString();
        assertThat(actual).contains("Content-Type: text/css");
    }

    @Test
    void Request와_비교하여_JSSESIONID를_내려주는_후처리를_한다() throws IOException {
        //given
        final String requestString = String.join("\r\n",
                "GET /index.html?a=1&b=2 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        final Request request = Request.from(bufferedReader);
        final Response response = new Response();

        //when
        response.postprocess(request);

        //then
        final String actual = response.parseString();
        assertThat(actual).contains("JSESSIONID");
    }
}
