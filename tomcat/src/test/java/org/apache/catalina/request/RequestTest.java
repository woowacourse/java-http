package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    @DisplayName("성공 : 쿼리 파라미터가 없으면 true를 반환한다.")
    void checkQueryParamIsEmptySuccessTrue() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());

        boolean actual = request.checkQueryParamIsEmpty();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("성공 : 쿼리 파라미터가 있으면 false를 반환한다.")
    void checkQueryParamIsEmptySuccessFalse() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());
        request.setQueryParam(Map.of("param", "value"));

        boolean actual = request.checkQueryParamIsEmpty();

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("성공 : body 값을 추가할 수 있다.")
    void setBody() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());
        Map<String, String> expected = Map.of("key", "value");

        request.setBody(expected);

        Map<String, String> actual = request.getBody();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : query param 값을 추가할 수 있다.")
    void setQueryParam() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());
        Map<String, String> expected = Map.of("param", "value");

        request.setQueryParam(expected);

        Map<String, String> actual = request.getQueryParam();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : Http Method를 알 수 있다.")
    void getHttpMethod() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());

        String actual = request.getHttpMethod();

        assertThat(actual).isEqualTo("GET");
    }

    @Test
    @DisplayName("성공 : query가 포함된 url을 알 수 있다.")
    void getUrlIncludeQuery() {
        Request request = new Request("GET /index?id=123 HTTP/1.1", Map.of());

        String actual = request.getUrlIncludeQuery();

        assertThat(actual).isEqualTo("/index?id=123");
    }

    @Test
    @DisplayName("성공 : query가 포함되지 않은 url을 알 수 있다.")
    void getUrl() {
        Request request = new Request("GET /index?id=123 HTTP/1.1", Map.of());

        String actual = request.getUrl();

        assertThat(actual).isEqualTo("/index");
    }

    @Nested
    @DisplayName("response 파일 타입을 알 수 있다.")
    class getFileType {
        @Test
        @DisplayName("성공 : response 시 원하는 파일 타입을 알 수 있다.")
        void getFileTypeSuccess() {
            Request request = new Request("GET /index.html HTTP/1.1", Map.of("Accept", "text/css"));

            String actual = request.getFileType();

            assertThat(actual).isEqualTo("text/css");
        }

        @Test
        @DisplayName("성공 : header에 해당 정보가 없으면 text/html을 기본 값으로 한다.")
        void getFileTypeSuccessByNotContainAccept() {
            Request request = new Request("GET /index.html HTTP/1.1", Map.of());

            String actual = request.getFileType();

            assertThat(actual).isEqualTo("text/html");
        }
    }

    @Test
    @DisplayName("성공 : 콘텐트 길이를 알 수 있다.")
    void getContentLengthSuccess() {
        int expected = 100;
        Request request = new Request("GET /index.html HTTP/1.1", Map.of("Content-Length", String.valueOf(expected)));

        int actual = request.getContentLength();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : header에 해당 정보가 없으면 0을 기본 값으로 한다.")
    void getContentLengthSuccessByNotContaionLength() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());

        int actual = request.getContentLength();

        assertThat(actual).isZero();
    }

    @Test
    @DisplayName("성공 : 쿠키를 알 수 있다.")
    void getCookie() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of("Cookie", "Id=324;JSessionId=34567"));

        HttpCookie actual = request.getCookie();

        HttpCookie expected = new HttpCookie(Map.of("Id", "324", "JSessionId", "34567"));
        assertThat(actual).isEqualTo(expected);
    }
}
