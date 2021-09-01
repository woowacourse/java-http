package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.EmptyQueryParametersException;
import nextstep.jwp.exception.QueryParameterNotFoundException;
import nextstep.jwp.http.common.HttpCookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String GET = "GET";
    private static final String URI = "/index.html";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() throws IOException {
        String requestString = String.join(NEW_LINE,
            String.format("%s %s %s", GET, URI, HTTP_VERSION),
            "",
            "");

        try (InputStream inputStream = new ByteArrayInputStream(
            requestString.getBytes(StandardCharsets.UTF_8))) {
            httpRequest = HttpRequest.parse(inputStream);
        }
    }

    @DisplayName("Method 일치 여부를 확인한다.")
    @Test
    void hasMethod() {
        // given
        Method methodGet = Method.matchOf(GET);
        Method methodPost = Method.matchOf("POST");

        // when, then
        assertThat(httpRequest.hasMethod(methodGet)).isTrue();
        assertThat(httpRequest.hasMethod(methodPost)).isFalse();
    }

    @DisplayName("Uri를 가져온다.")
    @Test
    void getUri() {
        // when
        String uri = httpRequest.getUri();

        // then
        assertThat(uri).isEqualTo(URI);
    }

    @DisplayName("Cookie 정보 요청시")
    @Nested
    class GetCookie {

        @DisplayName("Cookie를 가지고 있다면 HttpCookie를 반환한다.")
        @Test
        void getCookie() throws IOException {
            // given
            String requestLineWithQuery = String.format("%s %s %s", GET, URI, HTTP_VERSION);
            String requestString = String.join(NEW_LINE, requestLineWithQuery,
                "Cookie: wow=1234; ", "", "");

            try (InputStream inputStream = new ByteArrayInputStream(
                requestString.getBytes(StandardCharsets.UTF_8))) {
                httpRequest = HttpRequest.parse(inputStream);
            }

            // then
            assertThat(httpRequest.hasCookie()).isTrue();
            assertThat(httpRequest.getCookie()).isExactlyInstanceOf(HttpCookie.class);
        }

        @DisplayName("Cookie가 없다면 예외가 발생한다.")
        @Test
        void getCookieException() throws IOException {
            // given
            String requestLineWithQuery = String.format("%s %s %s", GET, URI, HTTP_VERSION);
            String requestString = String.join(NEW_LINE, requestLineWithQuery, "", "");

            try (InputStream inputStream = new ByteArrayInputStream(
                requestString.getBytes(StandardCharsets.UTF_8))) {
                httpRequest = HttpRequest.parse(inputStream);
            }

            // then
            assertThat(httpRequest.hasCookie()).isFalse();
            assertThatThrownBy(httpRequest::getCookie)
                .isExactlyInstanceOf(QueryParameterNotFoundException.class);
        }
    }

    @DisplayName("Uri Query와 함께 요청시")
    @Nested
    class RequestWithUriQuery {

        private static final String QUERY = "AAA";
        private static final String PARAMETER = "BBB";

        @BeforeEach
        void setUp() throws IOException {
            String uriWithQuery = String.format("%s?%s=%s", URI, QUERY, PARAMETER);
            String requestLineWithQuery = String.format("%s %s %s", GET, uriWithQuery,
                HTTP_VERSION);
            String requestString = String.join(NEW_LINE, requestLineWithQuery, "", "");

            try (InputStream inputStream = new ByteArrayInputStream(
                requestString.getBytes(StandardCharsets.UTF_8))) {
                httpRequest = HttpRequest.parse(inputStream);
            }
        }

        @DisplayName("uri Query Parameter 가 있다.")
        @Test
        void hasQueryParam() {
            assertThat(httpRequest.hasQueryParam()).isTrue();
        }

        @DisplayName("uri Query Parameter 를 요청하면 Parameter를 반환한다.")
        @Test
        void getUriParameter() {
            assertThat(httpRequest.getUriParameter(QUERY)).isEqualTo(PARAMETER);
        }

        @DisplayName("존재하지 않는 uri Query Parameter 를 요청하면 예외가 발생한다.")
        @Test
        void getUriParameterException() {
            assertThatThrownBy(() -> httpRequest.getUriParameter("something"))
                .isExactlyInstanceOf(QueryParameterNotFoundException.class);
        }
    }

    @DisplayName("Uri Query 없이 요청시")
    @Nested
    class RequestWithOutUriQuery {

        private final String requestLineWithOutQuery = String.format("%s %s %s", GET, URI,
            HTTP_VERSION);

        @BeforeEach
        void setUp() throws IOException {
            String requestString = String.join(NEW_LINE, requestLineWithOutQuery, "", "");

            try (InputStream inputStream = new ByteArrayInputStream(
                requestString.getBytes(StandardCharsets.UTF_8))) {
                httpRequest = HttpRequest.parse(inputStream);
            }
        }

        @DisplayName("uri Query Parameter 가 없다.")
        @Test
        void hasQueryParam() {
            assertThat(httpRequest.hasQueryParam()).isFalse();
        }

        @DisplayName("uri Query Parameter 를 요청하면 예외가 발생한다.")
        @Test
        void getUriParameterException() {
            assertThatThrownBy(() -> httpRequest.getUriParameter("something"))
                .isExactlyInstanceOf(EmptyQueryParametersException.class);
        }

        @DisplayName("Request-Body가 없다면")
        @Nested
        class RequestWithOutBody {

            private final String requestString = String.join(NEW_LINE, requestLineWithOutQuery, "",
                "");

            @DisplayName("body parameter 요청시 예외가 발생한다.")
            @Test
            void getBodyParameterException() throws IOException {
                // given
                try (InputStream inputStream = new ByteArrayInputStream(
                    requestString.getBytes(StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                // when, then
                assertThatThrownBy(() -> httpRequest.getBodyParameter("something"))
                    .isExactlyInstanceOf(EmptyQueryParametersException.class);
            }
        }

        @DisplayName("Request-Body가 있다면")
        @Nested
        class RequestWithBody {

            private static final String QUERY = "A";
            private static final String PARAMETER = "B";

            @BeforeEach
            void setUp() throws IOException {
                String body = String.format("%s=%s", QUERY, PARAMETER);
                int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

                String requestString = String.join(NEW_LINE,
                    requestLineWithOutQuery,
                    String.format("Content-Length: %d ", contentLength),
                    "",
                    body
                );

                try (InputStream inputStream = new ByteArrayInputStream(
                    requestString.getBytes(StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }
            }

            @DisplayName("body parameter 요청시 parameter를 반환한다.")
            @Test
            void getBodyParameter() {
                assertThat(httpRequest.getBodyParameter(QUERY)).isEqualTo(PARAMETER);
            }

            @DisplayName("존재하지 않는 body parameter 요청시 예외가 발생한다.")
            @Test
            void getBodyParameterException() {
                assertThatThrownBy(() -> httpRequest.getBodyParameter("noExistQuery"))
                    .isExactlyInstanceOf(QueryParameterNotFoundException.class);
            }
        }
    }
}