package nextstep.jwp.httpmessage;

import nextstep.jwp.httpmessage.httprequest.HttpMessageReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static nextstep.jwp.RequestHandlerTest.toHttpGetRequest;
import static nextstep.jwp.RequestHandlerTest.toHttpPostRequest;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpMessageReader 기능 테스트")
class HttpMessageReaderTest {

    @Test
    void getStartLine() {
        //given
        final String requestUri = "/test";
        final String input = toHttpGetRequest(requestUri);
        //when
        final HttpMessageReader httpMessageReader = new HttpMessageReader(new ByteArrayInputStream(input.getBytes()));
        final String actual = httpMessageReader.getStartLine();
        //then
        assertThat(actual).isEqualTo("GET " + requestUri + " HTTP/1.1");
    }

    @Test
    void getHeaders() {
        //given
        final String requestUri = "/test";
        final String input = toHttpGetRequest(requestUri);
        //when
        final HttpMessageReader httpMessageReader = new HttpMessageReader(new ByteArrayInputStream(input.getBytes()));
        final Map<String, String> headers = httpMessageReader.getHeaders();
        //then
        assertThat(headers.get("Host")).isEqualTo("localhost:8080");
        assertThat(headers.get("Connection")).isEqualTo("keep-alive");
    }

    @Test
    void getParameters() {
        //given
        final String requestUri = "/test";
        final String requestBody = "account=gumgum&password=password2&email=ggump%40woowahan.com";
        final String input = toHttpPostRequest(requestUri, requestBody);
        //when
        final HttpMessageReader httpMessageReader = new HttpMessageReader(new ByteArrayInputStream(input.getBytes()));
        final Map<String, String> parameters = httpMessageReader.getParameters();
        //then
        assertThat(parameters.get("account")).isEqualTo("gumgum");
        assertThat(parameters.get("password")).isEqualTo("password2");
        assertThat(parameters.get("email")).isEqualTo("ggump%40woowahan.com");
    }
}
