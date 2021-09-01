package nextstep.jwp.framework.http.response.details;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseHttpHeaderTest {

    @Test
    void appendResponseBodyInfo() {
        //given
        String responseBody = "<html>hello</html>";
        String extension = "html";
        final ResponseHttpHeader responseHttpHeader = new ResponseHttpHeader();
        responseHttpHeader.appendResponseBodyInfo(responseBody, extension);

        //when
        final String response = responseHttpHeader.generateResponse();
        final String expected = "Content-Length: 18\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n";
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void appendRedirectInfo() {
        //given
        String location = "/hello";
        final ResponseHttpHeader responseHttpHeader = new ResponseHttpHeader();
        responseHttpHeader.appendRedirectInfo(location);

        //when
        final String response = responseHttpHeader.generateResponse();
        final String expected = "Location: /hello\r\n";
        assertThat(response).isEqualTo(expected);
    }
}
