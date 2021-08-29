package nextstep.jwp.http.request;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void of() {
        final String expectedRequestLine = "POST /register HTTP/1.1 ";
        final String expectedRequestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String inputRequest = mockRequest(expectedRequestLine, expectedRequestBody);

        try (final InputStream inputStream = new ByteArrayInputStream(inputRequest.getBytes())) {
            HttpRequest parsedRequest = HttpRequest.of(inputStream);

            RequestLine requestLine = parsedRequest.getRequestLine();
            assertThat(requestLine).isEqualTo(RequestLine.of(expectedRequestLine));

            RequestHeaders requestHeaders = parsedRequest.getRequestHeaders();
            assertThat(requestHeaders.contentLength()).isEqualTo(expectedRequestBody.length());

            String body = parsedRequest.getRequestBody();
            assertThat(body).isEqualTo(expectedRequestBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String mockRequest(String expectedRequestLine, String expectedRequestBody) {
        return String.join("\r\n",
                expectedRequestLine,
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + expectedRequestBody.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                expectedRequestBody,
                "");
    }
}