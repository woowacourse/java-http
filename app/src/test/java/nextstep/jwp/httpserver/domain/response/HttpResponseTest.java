package nextstep.jwp.httpserver.domain.response;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.Cookie;
import nextstep.jwp.httpserver.domain.HttpVersion;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpResponse 단위 테스트")
class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse 빌더 패턴 테스트")
    void builder() {
        // given
        // when
        HttpResponse httpResponse = new HttpResponse.Builder()
                .statusCode(StatusCode.CREATED)
                .header("Location", "1")
                .cookies(Collections.singletonList(new Cookie("air", "name")))
                .body("name=air")
                .build();

        // then
        assertThat(httpResponse.getStatusLine().getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(StatusCode.CREATED);
        assertThat(httpResponse.getHeaders().getHeaders()).containsKey("Location");
        assertThat(httpResponse.getCookies()).hasSize(1);
        assertThat(httpResponse.getBody()).isNotNull();
    }

    @Test
    @DisplayName("HttpResponse에서 statusLine 출력하기")
    void statusLine() {
        // given
        HttpResponse httpResponse = new HttpResponse.Builder()
                .statusCode(StatusCode.CREATED)
                .header("Location", "1")
                .build();

        // when
        String statusLine = httpResponse.statusLine();

        // then
        assertThat(statusLine).isEqualTo("HTTP/1.1 201 Created ");
    }

    @Test
    @DisplayName("응답을 200으로 변경")
    void ok() {
        // given
        HttpResponse httpResponse = new HttpResponse();

        // when
        httpResponse.ok();

        // then
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(StatusCode.OK);
    }

    @Test
    @DisplayName("응답을 302로 변경 & 헤더에 Location 추가")
    void redirect() {
        // given
        HttpResponse httpResponse = new HttpResponse();

        // when
        httpResponse.redirect("/index.html");

        // then
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(StatusCode.FOUND);
        assertThat(httpResponse.getHeaders().getHeaders()).containsKey("Location");
    }

    @Test
    @DisplayName("헤더 값 추가")
    void addHeader() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        int originalSize = httpResponse.getHeaders().getHeaders().size();

        // when
        httpResponse.addHeader("Location", "/index.html");

        // then
        assertThat(httpResponse.getHeaders().getHeaders().size()).isEqualTo(originalSize + 1);
        assertThat(httpResponse.getHeaders().getHeaders()).containsKey("Location");
    }

    @Test
    @DisplayName("쿠키값 추가")
    void addCookie() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        int originalSize = httpResponse.getCookies().size();
        Cookie cookie = new Cookie("name", "air");

        // when
        httpResponse.addCookie(cookie);

        // then
        assertThat(httpResponse.getCookies()).hasSize(originalSize + 1);
        assertThat(httpResponse.getCookies().get(0).getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("응답을 String으로 전환")
    void result() {
        // given
        HttpResponse httpResponse = new HttpResponse.Builder()
                .statusCode(StatusCode.CREATED)
                .header("Location", "/users/1")
                .cookies(Collections.singletonList(new Cookie("JSESSIONID", "ABCDE")))
                .body("Hello world!")
                .build();

        // when
        String result = httpResponse.getResult();

        // then
        assertThat(result).contains(
                "HTTP/1.1 201 Created",
                "Location: /users/1",
                "Set-Cookie: JSESSIONID=ABCDE",
                "Hello world!"
        );
    }
}
