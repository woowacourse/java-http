package nextstep.jwp.httpmessage;

import nextstep.jwp.RequestHandlerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpMessageReader 기능 테스트")
class HttpMessageReaderTest {

    @Test
    void getStartLine() {
        //given
        final String requestUri = "/test";
        final String input = RequestHandlerTest.toHttpGetRequest(requestUri);
        //when
        final HttpMessageReader httpMessageReader = new HttpMessageReader(new ByteArrayInputStream(input.getBytes()));
        final String actual = httpMessageReader.getStartLine();
        //then
        assertThat(actual).isEqualTo("GET " + requestUri + " HTTP/1.1");
    }

    @Test
    void return_null_when_stream_is_empty() {
        //given
        final String input = "GET /test HTTP/1.1 \r\n";
        //when
        final HttpMessageReader httpMessageReader = new HttpMessageReader(new ByteArrayInputStream(input.getBytes()));
        httpMessageReader.getStartLine();
        final String actual = httpMessageReader.getStartLine();
        //then
        assertThat(actual).isEmpty();
    }
}