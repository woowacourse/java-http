package org.apache.catalina.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResponseHeaderTest {

    @ParameterizedTest
    @ValueSource(strings = {"text/html", "text/css", "*/*"})
    @DisplayName("성공 : 컨텐츠 타입 설정")
    void setContentType(String contentType) {
        ResponseHeader responseHeader = new ResponseHeader();

        responseHeader.setContentType(contentType);

        String actual = responseHeader.toString();
        String expected = "Content-Type: " + contentType + " \r\n";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : 컨텐츠 길이 설정")
    void setContentLength() {
        String contentLength = "1000";
        ResponseHeader responseHeader = new ResponseHeader();

        responseHeader.setContentLength(contentLength);

        String actual = responseHeader.toString();
        String expected = "Content-Length: " + contentLength + " \r\n";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : 쿠키 설정")
    void setCookie() {
        String cookie = "ID=dsaf9e-dfs0-e3";
        ResponseHeader responseHeader = new ResponseHeader();

        responseHeader.setCookie(cookie);

        String actual = responseHeader.toString();
        String expected = "Set-Cookie: " + cookie + " \r\n";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : location 설정")
    void setLocation() {
        String location = "ID=dsaf9e-dfs0-e3";
        ResponseHeader responseHeader = new ResponseHeader();

        responseHeader.setRedirection(location);

        String actual = responseHeader.toString();
        String expected = "Location: " + location + " \r\n";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : 헤더 키-값 추가")
    void add() {
        String key = "key";
        String value = "value";
        ResponseHeader responseHeader = new ResponseHeader();

        responseHeader.add(key, value);

        String actual = responseHeader.toString();
        String expected = key + ": " + value + " \r\n";
        assertThat(actual).isEqualTo(expected);
    }
}
