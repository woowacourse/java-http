package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    @DisplayName("성공 : body 값 추가 가능")
    void setBody() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());
        Map<String, String> expected = Map.of("key", "value");

        request.setBody(expected);

        Map<String, String> actual = request.getBody();
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("response 파일 타입 조회")
    class getFileType {
        @Test
        @DisplayName("성공 : response 시 원하는 파일 타입 조회")
        void getFileTypeSuccess() {
            Request request = new Request("GET /index.html HTTP/1.1", Map.of("Accept", "text/css"));

            String actual = request.getFileType();

            assertThat(actual).isEqualTo("text/css");
        }

        @Test
        @DisplayName("성공 : header에 해당 정보가 없으면 text/html을 기본 값으로 설정")
        void getFileTypeSuccessByNotContainAccept() {
            Request request = new Request("GET /index.html HTTP/1.1", Map.of());

            String actual = request.getFileType();

            assertThat(actual).isEqualTo("text/html");
        }
    }

    @Test
    @DisplayName("성공 : 콘텐트 길이 조회 가능")
    void getContentLengthSuccess() {
        int expected = 100;
        Request request = new Request("GET /index.html HTTP/1.1", Map.of("Content-Length", String.valueOf(expected)));

        int actual = request.getContentLength();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : header에 해당 정보가 없으면 0을 기본 값으로 설정")
    void getContentLengthSuccessByNotContaionLength() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of());

        int actual = request.getContentLength();

        assertThat(actual).isZero();
    }

    @Test
    @DisplayName("성공 : 쿠키를 조회 가능")
    void getCookie() {
        Request request = new Request("GET /index.html HTTP/1.1", Map.of("Cookie", "Id=324;JSessionId=34567"));

        HttpCookie actual = request.getCookie();

        HttpCookie expected = new HttpCookie(Map.of("Id", "324", "JSessionId", "34567"));
        assertThat(actual).isEqualTo(expected);
    }
}
