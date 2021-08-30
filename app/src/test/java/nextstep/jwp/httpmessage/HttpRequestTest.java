package nextstep.jwp.httpmessage;

import nextstep.jwp.RequestHandlerTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@DisplayName("HttpRequest 테스트")
class HttpRequestTest {

    @Test
    void getRequestLine() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpGetRequest(requestUri);
        //when
        final InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());

        final HttpRequest httpRequest = new HttpRequest(new HttpMessageReader(inputStream));
        //then
        Assertions.assertThat(httpRequest.getRequestLine()).isEqualTo("GET " + requestUri + " HTTP/1.1");
    }

    @Test
    void postRequestLine() {
        //given
        final String requestUri = "/test";
        final String httpMessage = RequestHandlerTest.toHttpPostRequest(requestUri, "");
        //when
        final InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());

        final HttpRequest httpRequest = new HttpRequest(new HttpMessageReader(inputStream));
        //then
        Assertions.assertThat(httpRequest.getRequestLine()).isEqualTo("POST " + requestUri + " HTTP/1.1");
    }
}