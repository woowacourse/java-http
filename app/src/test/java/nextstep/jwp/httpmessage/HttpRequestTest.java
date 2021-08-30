package nextstep.jwp.httpmessage;

import nextstep.jwp.RequestHandlerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpRequest 테스트")
class HttpRequestTest {

    @Test
    void getRequestLine() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpGetRequest(requestUri);
        //when
        final HttpRequest httpRequest = toHttpRequest(httpMessage);
        //then
        assertThat(httpRequest.getRequestLine()).isEqualTo("GET " + requestUri + " HTTP/1.1");
    }

    @Test
    void postRequestLine() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpPostRequest(requestUri, "");
        //when
        final HttpRequest httpRequest = toHttpRequest(httpMessage);
        //then
        assertThat(httpRequest.getRequestLine()).isEqualTo("POST " + requestUri + " HTTP/1.1");
    }

    @Test
    void getMethod() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpGetRequest(requestUri);
        //when
        final HttpRequest httpRequest = toHttpRequest(httpMessage);
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        //then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void postMethod() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpPostRequest(requestUri, "");
        //when
        final HttpRequest httpRequest = toHttpRequest(httpMessage);
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        //then
        assertThat(httpMethod).isEqualTo(HttpMethod.POST);
    }

    @Test
    void path() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpPostRequest(requestUri, "");
        //when
        final HttpRequest httpRequest = toHttpRequest(httpMessage);
        String path = httpRequest.getPath();
        //then
        assertThat(path).isEqualTo(requestUri);
    }

    @Test
    void versionOfTheProtocol() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpPostRequest(requestUri, "");
        //when
        final HttpRequest httpRequest = toHttpRequest(httpMessage);
        String versionOfTheProtocol = httpRequest.getVersionOfTheProtocol();
        //then
        assertThat(versionOfTheProtocol).isEqualTo("HTTP/1.1");
    }


    private HttpRequest toHttpRequest(String httpMessage) {
        final InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        return new HttpRequest(new HttpMessageReader(inputStream));
    }
}