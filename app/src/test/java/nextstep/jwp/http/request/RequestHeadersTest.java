package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.exception.HttpRequestNotHaveBodyException;
import nextstep.jwp.exception.InvalidRequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    @DisplayName("Request-Header는 대소문자를 구분하지 않는다.")
    @Test
    void noCareUpperLower() throws IOException {
        // given
        String inputHeaders = String.join(NEW_LINE, "Content-length: wow", "ABC: DEFGG", "", "");

        BufferedReader bufferedReader;

        try (InputStream inputStream = new ByteArrayInputStream(inputHeaders.getBytes())) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        }

        assertThat(RequestHeaders.parse(bufferedReader)).isExactlyInstanceOf(RequestHeaders.class);
    }

    @DisplayName("Request-Header 포맷에 맞지 않을 경우 예외가 발생한다.")
    @Test
    void InvalidFormatHeader() throws IOException {
        // given
        String inputHeaders = String.join(NEW_LINE, "Content-length wow", "ABC:", "", "");

        BufferedReader bufferedReader;

        try (InputStream inputStream = new ByteArrayInputStream(inputHeaders.getBytes())) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        }

        assertThatThrownBy(() -> RequestHeaders.parse(bufferedReader))
            .isExactlyInstanceOf(InvalidRequestHeader.class);
    }

    @DisplayName("Content-Length 헤더가 포함되었다면")
    @Nested
    class includeContentLength {

        private final int contentLengthValue = 100;
        private final String contentLength = String.format("Content-Length: %d", contentLengthValue);
        private final String inputHeaders = String.join(NEW_LINE, contentLength, "", "");

        @DisplayName("Content-Length 헤더로 Request-Body 가 있음을 판단한다.")
        @Test
        void requestHasBody() throws IOException {
            RequestHeaders requestHeaders;

            try (InputStream inputStream = new ByteArrayInputStream(inputHeaders.getBytes())) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                requestHeaders = RequestHeaders.parse(bufferedReader);
            }

            assertThat(requestHeaders.requestHasBody()).isTrue();
        }

        @DisplayName("Content-Length 값을 정수로 반환한다.")
        @Test
        void getContentLength() throws IOException {
            RequestHeaders requestHeaders;

            try (InputStream inputStream = new ByteArrayInputStream(inputHeaders.getBytes())) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                requestHeaders = RequestHeaders.parse(bufferedReader);
            }

            assertThat(requestHeaders.getContentLength()).isEqualTo(contentLengthValue);
        }
    }

    @DisplayName("Content-Length 헤더가 없다면")
    @Nested
    class excludeContentLength {

        private final String inputHeaders = String.join(NEW_LINE, "something: wow", "", "");

        @DisplayName("Content-Length 헤더로 Request-Body 가 없음을 판단한다.")
        @Test
        void requestHasBody() throws IOException {
            RequestHeaders requestHeaders;

            try (InputStream inputStream = new ByteArrayInputStream(inputHeaders.getBytes())) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                requestHeaders = RequestHeaders.parse(bufferedReader);
            }

            assertThat(requestHeaders.requestHasBody()).isFalse();
        }

        @DisplayName("Content-Length 요청시 예외가 발생한다.")
        @Test
        void getContentLength() throws IOException {
            RequestHeaders requestHeaders;

            try (InputStream inputStream = new ByteArrayInputStream(inputHeaders.getBytes())) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                requestHeaders = RequestHeaders.parse(bufferedReader);
            }

            assertThatThrownBy(requestHeaders::getContentLength)
                .isExactlyInstanceOf(HttpRequestNotHaveBodyException.class);
        }
    }
}