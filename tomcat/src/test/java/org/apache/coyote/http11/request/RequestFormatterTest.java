package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static utils.TestFixtures.NEW_LINE;

class RequestFormatterTest {

    @Test
    @DisplayName("문자열로 입력받은 requestLine을 HttpRequestLine 형식에 맞게 포맷팅한다.")
    void toReqeustLineFormat() {
        //given
        String requestLine = "POST /index HTTP/1.1";

        //when
        String[] result = RequestFormatter.toRequestLineFormat(requestLine);

        //then
        assertAll(
                () -> assertThat(result).hasSize(3),
                () -> assertThat(result[0]).isEqualTo("POST"),
                () -> assertThat(result[1]).isEqualTo("/index"),
                () -> assertThat(result[2]).isEqualTo("HTTP/1.1")
        );
    }

    @NullAndEmptySource
    @CsvSource({"method url ", "method url version error", "POST  /index HTTP/1.1"})
    @ParameterizedTest
    @DisplayName("올바르지 않은 requestLine 포맷팅 요청이 들어오면 IllegalArgumentException을 반환한다.")
    void toRequestLineFormat_invalidInput(String requestLine) {
        //when, then
        assertThatThrownBy(() -> RequestFormatter.toRequestLineFormat(requestLine))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("정상적인 헤더 입력을 처리한다.")
    void toHeader() throws IOException {
        // given
        String input = String.join(NEW_LINE,
                "Content-Length: 128",
                "Cookie: testCookie",
                "");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));

        // when
        RequestHeader requestHeader = RequestFormatter.toHeader(bufferedReader);

        // then
        assertThat(requestHeader.getContentLength()).isEqualTo(128);
        assertThat(requestHeader.getCookies()).isEqualTo("testCookie");
    }

    @EmptySource
    @ParameterizedTest
    @DisplayName("빈 입력을 처리한다.")
    void toHeader_emptyInput(String input) throws IOException {
        // given
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));

        // when
        RequestHeader requestHeader = RequestFormatter.toHeader(bufferedReader);

        // then
        assertThat(requestHeader.getContentLength()).isZero();
        assertThat(requestHeader.getCookies()).isNull();
    }

    @Test
    @DisplayName("헤더 라인에 공백이 포함된 경우를 처리한다.")
    void toHeader_headerWithSpaces() throws IOException {
        // given
        String input = String.join(NEW_LINE,
                "Content-Length: 128",
                "Cookie: testCookie",
                "");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));

        // when
        RequestHeader requestHeader = RequestFormatter.toHeader(bufferedReader);

        // then
        assertThat(requestHeader.getContentLength()).isEqualTo(128);
        assertThat(requestHeader.getCookies()).isEqualTo("testCookie");
    }

    @Test
    @DisplayName("잘못된 헤더 포맷을 처리한다.")
    void toHeader_invalidHeaderFormat() {
        // given
        String input = String.join(NEW_LINE,
                "InvalidHeaderFormat" + NEW_LINE,
                "");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));

        //when, then
        assertThatThrownBy(() -> RequestFormatter.toHeader(bufferedReader))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("GET 요청일 경우 빈 RequestBody를 반환한다.")
    void toBody_getRequest_returnsEmpty() throws IOException {
        // given
        String input = "Content-Length: 128" + NEW_LINE + NEW_LINE;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
        boolean isGet = true;
        int contentLength = 128;

        // when
        RequestBody result = RequestFormatter.toBody(bufferedReader, isGet, contentLength);

        // then
        assertThat(result.getContents()).isEmpty();
    }

    @Test
    @DisplayName("POST 요청일 경우 Content-Length에 맞게 RequestBody를 반환한다.")
    void toBody_postRequest_returnsCorrectBody() throws IOException {
        // given
        String input = "account=gugu&password=password";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
        boolean isGet = false;

        // when
        RequestBody result = RequestFormatter.toBody(bufferedReader, isGet, input.length());

        // then
        assertAll(
                () -> assertThat(result.getContents()).containsEntry("account", "gugu"),
                () -> assertThat(result.getContents()).containsEntry("password", "password")
        );
    }

    @Test
    @DisplayName("Content-Length가 0일 때 빈 Body를 반환한다.")
    void toBody_postRequest_zeroContentLength() throws IOException {
        // given
        String input = "Content-Length: 0" + NEW_LINE + NEW_LINE;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
        boolean isGet = false;
        int contentLength = 0;

        // when
        RequestBody result = RequestFormatter.toBody(bufferedReader, isGet, contentLength);

        // then
        assertThat(result.getContents()).isEmpty();
    }
}
