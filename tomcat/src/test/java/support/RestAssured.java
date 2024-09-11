package support;

import java.util.Map;
import java.util.Optional;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.body.HttpRequestBody;
import org.apache.catalina.http.body.HttpResponseBody;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpMethod;
import org.apache.catalina.http.startline.HttpRequestLine;
import org.apache.catalina.http.startline.HttpResponseLine;
import org.apache.catalina.http.startline.HttpStatus;
import org.apache.catalina.http.startline.HttpVersion;
import org.apache.catalina.http.startline.RequestURI;
import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.servlet.RequestMapper;
import org.assertj.core.api.Assertions;

public class RestAssured {

    private final HttpResponseLine responseLine;
    private final HttpHeaders responseHeaders;
    private final HttpResponseBody responseBody;

    private RestAssured(HttpResponseLine responseLine, HttpHeaders responseHeaders, HttpResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static RestAssuredRequestGenerator when() {
        return new RestAssuredRequestGenerator();
    }

    public RestAssured assertThat() {
        return this;
    }

    public RestAssured httpStatusIs(HttpStatus expected) {
        Assertions.assertThat(responseLine.getStatus()).isEqualTo(expected);
        return this;
    }

    public RestAssured responseBodyIs(String expected) {
        Assertions.assertThat(responseBody.getValue()).isEqualTo(expected);
        return this;
    }

    public RestAssured responseBodyContains(String expectedPart) {
        Assertions.assertThat(responseBody.getValue()).contains(expectedPart);
        return this;
    }

    public RestAssured containsHeader(HttpHeader expected, String value) {
        if (HttpHeader.COOKIE == expected) {
            throw new RestAssuredException("Cookie header not supported. Use containsCookie() instead.");
        }
        Assertions.assertThatCode(() -> responseHeaders.get(expected))
                .doesNotThrowAnyException();
        Assertions.assertThat(responseHeaders.get(expected)).isEqualTo(value);
        return this;
    }

    public RestAssured containsCookie(String key) {
        Assertions.assertThatCode(() -> responseHeaders.getFromCookies(key))
                .doesNotThrowAnyException();
        return this;
    }

    public Optional<String> getFromCookies(String key) {
        return responseHeaders.getFromCookies(key);
    }

    public static class RestAssuredRequestGenerator {

        private HttpRequestLine requestLine;
        private HttpHeaders requestHeaders = new HttpHeaders();
        private HttpRequestBody requestBody = new HttpRequestBody();

        RestAssuredRequestGenerator() {

        }

        public RestAssuredRequestGenerator post(String uri) {
            this.requestLine = new HttpRequestLine(HttpMethod.POST, new RequestURI(uri), HttpVersion.HTTP11);
            return this;
        }

        public RestAssuredRequestGenerator get(String uri) {
            this.requestLine = new HttpRequestLine(HttpMethod.GET, new RequestURI(uri), HttpVersion.HTTP11);
            return this;
        }

        public RestAssuredRequestGenerator header(HttpHeader header, String value) {
            if (HttpHeader.COOKIE == header) {
                throw new RestAssuredException("Cookie header not supported. Use cookie() instead.");
            }
            requestHeaders.add(header, value);
            return this;
        }

        public RestAssuredRequestGenerator cookie(String key, String value) {
            requestHeaders.addToCookies(key, value);
            return this;
        }

        public RestAssuredRequestGenerator body(Map<String, String> body) {
            this.requestBody = new HttpRequestBody(body);
            return this;
        }

        public RestAssured then() {
            HttpRequest request = new HttpRequest(requestLine, requestHeaders, requestBody);
            AbstractController controller = RequestMapper.getController(request);

            HttpResponseLine responseLine = new HttpResponseLine(request.getHttpVersion());
            HttpHeaders responseHeaders = new HttpHeaders();
            HttpResponseBody responseBody = new HttpResponseBody();
            HttpResponse response = new HttpResponse(responseLine, responseHeaders, responseBody);

            controller.service(request, response);
            return new RestAssured(responseLine, responseHeaders, responseBody);
        }
    }
}
