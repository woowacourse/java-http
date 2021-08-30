package nextstep.jwp.httpmessage;

import nextstep.jwp.ContentType;
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

    private static Stream<Arguments> getRequestLineAndHeadersWhenGetRequest() {
        return Stream.of(
                Arguments.of("/test", ContentType.HTML),
                Arguments.of("/test/test", ContentType.CSS),
                Arguments.of("/test/you", ContentType.IMAGE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getRequestLineAndHeadersWhenGetRequest(String requestUri, ContentType contentType) {
        //given
        //when
        final HttpRequest httpRequest = new HttpRequest(new HttpMessageReaderGetStub(requestUri, contentType));
        final HttpHeaders httpHeaders = httpRequest.getHttpHeaders();
        //then
        assertThat(httpRequest.getRequestLine()).isEqualTo(HttpMethod.GET + " " + requestUri + " HTTP/1.1");
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequest.getPath()).isEqualTo(requestUri);
        assertThat(httpRequest.getVersionOfTheProtocol()).isEqualTo("HTTP/1.1");
        assertThat(httpHeaders.find("Content-Type")).isEqualTo(contentType.getContentType());
        assertThat(httpHeaders.size()).isNotEqualTo(0);
    }

    private static Stream<Arguments> getRequestLineAndHeadersWhenPostRequest() {
        return Stream.of(
                Arguments.of("/test", ContentType.X_WWW_FORM),
                Arguments.of("/test/test", ContentType.X_WWW_FORM),
                Arguments.of("/test/you", ContentType.X_WWW_FORM)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getRequestLineAndHeadersWhenPostRequest(String requestUri, ContentType contentType) {
        //given
        //when
        final HttpRequest httpRequest = new HttpRequest(new HttpMessageReaderPostStub(requestUri, contentType));
        final HttpHeaders httpHeaders = httpRequest.getHttpHeaders();
        //then
        assertThat(httpRequest.getRequestLine()).isEqualTo(HttpMethod.POST + " " + requestUri + " HTTP/1.1");
        assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(httpRequest.getPath()).isEqualTo(requestUri);
        assertThat(httpRequest.getVersionOfTheProtocol()).isEqualTo("HTTP/1.1");
        assertThat(httpHeaders.find("Content-Type")).isEqualTo(contentType.getContentType());
        assertThat(httpHeaders.size()).isNotEqualTo(0);
    }
}