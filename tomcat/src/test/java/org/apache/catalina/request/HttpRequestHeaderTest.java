package org.apache.catalina.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @Nested
    @DisplayName("response 파일 타입 조회")
    class getFileType {
        @Test
        @DisplayName("성공 : response 시 원하는 파일 타입 조회")
        void getFileTypeSuccess() {
            RequestHeader requestHeader = new RequestHeader(Map.of("Accept", "text/css"));

            ContentType actual = requestHeader.getContentType();

            assertThat(actual).isEqualTo(ContentType.CSS);
        }

        @Test
        @DisplayName("성공 : header에 해당 정보가 없으면 text/html을 기본 값으로 설정")
        void getFileTypeSuccessByNotContainAccept() {
            RequestHeader requestHeader = new RequestHeader(Map.of());

            ContentType actual = requestHeader.getContentType();

            assertThat(actual).isEqualTo(ContentType.HTML);
        }
    }

    @Nested
    @DisplayName("콘텐트 길이 조회")
    class getContentLength {

        @Test
        @DisplayName("성공 : 콘텐트 길이 조회 가능")
        void getContentLengthSuccess() {
            int expected = 100;
            RequestHeader requestHeader = new RequestHeader(Map.of("Content-Length", String.valueOf(expected)));

            int actual = requestHeader.getContentLength();

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("성공 : header에 해당 정보가 없으면 0을 기본 값으로 설정")
        void getContentLengthSuccessByNotContainLength() {
            RequestHeader requestHeader = new RequestHeader(Map.of());

            int actual = requestHeader.getContentLength();

            assertThat(actual).isZero();
        }
    }

    @Test
    @DisplayName("성공 : 쿠키를 조회 가능")
    void getCookie() {
        RequestHeader requestHeader = new RequestHeader(Map.of("Cookie", "Id=324;JSessionId=34567"));

        HttpCookie actual = requestHeader.getCookie();

        HttpCookie expected = new HttpCookie(Map.of("Id", "324", "JSessionId", "34567"));
        assertThat(actual).isEqualTo(expected);
    }
}
