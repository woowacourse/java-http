package nextstep.jwp.httpmessage;

import nextstep.jwp.httpmessage.httprequest.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestLine 테스트")
class RequestLineTest {

    @ParameterizedTest
    @CsvSource(value = {"/test,GET", "/test/post,POST"}, delimiter = ',')
    void getRequestLine(String expectPath, HttpMethod expectHttpMethod) {
        //given
        final String expectVersionOfProtocol = "HTTP/1.1";
        final String expectRequestLine = expectHttpMethod + " " + expectPath + " " + expectVersionOfProtocol;
        //when
        final RequestLine requestLine = new RequestLine(expectRequestLine);
        //then
        assertThat(requestLine.getLine()).isEqualTo(expectRequestLine);
        assertThat(requestLine.getMethod()).isEqualTo(expectHttpMethod);
        assertThat(requestLine.getPath()).isEqualTo(expectPath);
        assertThat(requestLine.getVersionOfTheProtocol()).isEqualTo(expectVersionOfProtocol);
    }
}