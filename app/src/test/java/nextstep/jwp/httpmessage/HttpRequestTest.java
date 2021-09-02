package nextstep.jwp.httpmessage;

import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.stub.HttpMessageReaderGetStub;
import nextstep.jwp.httpmessage.stub.HttpMessageReaderPostStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpRequest 테스트")
class HttpRequestTest {

    private static Stream<Arguments> getRequestLineAndHeadersAndParametersWhenGetRequest() {
        return Stream.of(
                Arguments.of("/test", ContentType.HTML),
                Arguments.of("/test/test", ContentType.CSS),
                Arguments.of("/test/you", ContentType.IMAGE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getRequestLineAndHeadersAndParametersWhenGetRequest(String requestUri, ContentType contentType) {
        //given
        //when
        final HttpRequest httpRequest = new HttpRequest(new HttpMessageReaderGetStub(requestUri, contentType));
        //then
        assertThat(httpRequest.getRequestLine()).isEqualTo(HttpMethod.GET + " " + requestUri + " HTTP/1.1");
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getPath()).isEqualTo(requestUri);
        assertThat(httpRequest.getVersionOfTheProtocol()).isEqualTo("HTTP/1.1");
        assertThat(httpRequest.getHeader("Content-Type")).isEqualTo(contentType.getValue());
        assertThat(httpRequest.httpHeaderSize()).isNotEqualTo(0);
        assertThat(httpRequest.getParameterNames().hasMoreElements()).isFalse();
    }

    private static Stream<Arguments> getRequestLineAndHeadersAndParametersWhenPostRequest() {
        return Stream.of(
                Arguments.of("/test", ContentType.X_WWW_FORM),
                Arguments.of("/test/test", ContentType.X_WWW_FORM),
                Arguments.of("/test/you", ContentType.X_WWW_FORM)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getRequestLineAndHeadersAndParametersWhenPostRequest(String requestUri, ContentType contentType) {
        //given
        //when
        final HttpRequest httpRequest = new HttpRequest(new HttpMessageReaderPostStub(requestUri, contentType));
        //then
        assertThat(httpRequest.getRequestLine()).isEqualTo(HttpMethod.POST + " " + requestUri + " HTTP/1.1");
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(httpRequest.getPath()).isEqualTo(requestUri);
        assertThat(httpRequest.getVersionOfTheProtocol()).isEqualTo("HTTP/1.1");
        assertThat(httpRequest.getHeader("Content-Type")).isEqualTo(contentType.getValue());
        assertThat(httpRequest.getParameterNames().hasMoreElements()).isTrue();
        assertThat(httpRequest.getParameter("account")).isEqualTo("gumgum");
    }
}
