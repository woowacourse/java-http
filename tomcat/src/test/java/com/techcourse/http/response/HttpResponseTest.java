package com.techcourse.http.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.http.ContentType;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("200 ok 응답 생성")
    @Test
    void okTest() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        ContentType contentType = ContentType.TEXT_HTML;
        HttpCookie httpCookie = HttpCookie.empty();
        ResponseBody responseBody = ResponseBody.helloWorld();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, httpCookie, responseBody);

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
        HttpCookie httpCookie = HttpCookie.empty();
        ResponseBody responseBody = ResponseBody.empty();

        // when
        HttpResponse httpResponse = HttpResponse.noContent(httpVersion, contentType, httpCookie, responseBody);

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
        HttpResponse httpResponse = HttpResponse.found(httpVersion, location, ContentType.APPLICATION_JSON, httpCookie);

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
        HttpCookie httpCookie = HttpCookie.from("JSESSIONID=abc123; theme=dark");
        ResponseBody responseBody = ResponseBody.helloWorld();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, httpCookie, responseBody);

        // then
        String responseString = new String(httpResponse.toBytes());

        assertAll(
                () -> assertThat(responseString).contains("Set-Cookie: JSESSIONID=abc123; theme=dark"),
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
        HttpCookie httpCookie = HttpCookie.empty();
        ResponseBody responseBody = ResponseBody.helloWorld();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, httpCookie, responseBody);

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
        HttpResponse httpResponse = HttpResponse.found(httpVersion, location, ContentType.APPLICATION_JSON, httpCookie);

        // then
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).doesNotContain("Location:");
    }

    @DisplayName("빈 응답인 경우")
    @Test
    void toBytesTest4() {
        // given
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        ContentType contentType = ContentType.TEXT_HTML;
        HttpCookie httpCookie = HttpCookie.empty();
        ResponseBody responseBody = ResponseBody.empty();

        // when
        HttpResponse httpResponse = HttpResponse.ok(httpVersion, contentType, httpCookie, responseBody);

        // then
        String responseString = new String(httpResponse.toBytes());
        assertThat(responseString).contains("Content-Length: 0");
        assertThat(responseString).endsWith("\r\n\r\n");
    }
}
