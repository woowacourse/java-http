package com.techcourse.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.Location;
import org.apache.coyote.http.response.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("200 ok 응답 생성")
    @Test
    void okTest() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        ContentType contentType = ContentType.TEXT_HTML;
        ResponseBody responseBody = ResponseBody.helloWorld();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, responseBody);

        // then
        assertThat(httpResponse).isNotNull();
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).contains("HTTP/1.1 200 OK");
    }

    @DisplayName("204 no content 응답 생성")
    @Test
    void noContentTest() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        ContentType contentType = ContentType.APPLICATION_JSON;

        // when
        HttpResponse httpResponse = HttpResponse.noContent(httpVersion, contentType);

        // then
        assertThat(httpResponse).isNotNull();
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).contains("HTTP/1.1 204 No Content");
    }

    @DisplayName("302 found 응답 생성")
    @Test
    void foundTest() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        Location location = new Location("/index.html");
        HttpCookie httpCookie = HttpCookie.empty();

        // when
        HttpResponse httpResponse = HttpResponse.found(httpVersion, location, httpCookie);

        // then
        assertThat(httpResponse).isNotNull();
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).contains("HTTP/1.1 302 Found");
        assertThat(responseString).contains("Location: /index.html");
    }

    @DisplayName("쿠키가 있는 응답을 생성하는 경우")
    @Test
    void toBytesTest1() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        ContentType contentType = ContentType.TEXT_HTML;
        ResponseBody responseBody = ResponseBody.helloWorld();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, responseBody);

        // then
        String responseString = new String(httpResponse.toBytes());

        assertAll(
                () -> assertThat(responseString).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(responseString).contains("Content-Length: 12") // "Hello world!" = 12 bytes
        );
    }

    @DisplayName("쿠키가 없는 응답을 생성하는 경우")
    @Test
    void toBytesTest2() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        ContentType contentType = ContentType.TEXT_HTML;
        ResponseBody responseBody = ResponseBody.helloWorld();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, responseBody);

        // then
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).doesNotContain("Set-Cookie");
    }

    @DisplayName("위치가 없는 응답을 생성하는 경우")
    @Test
    void toBytesTest3() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        Location location = Location.empty();
        HttpCookie httpCookie = HttpCookie.empty();

        // when
        HttpResponse httpResponse = HttpResponse.found(httpVersion, location, httpCookie);

        // then
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).doesNotContain("Location:");
    }
}
