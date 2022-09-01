package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

class ResponseProcessorTest {

    @Test
    void getResponse를_하면_http_형식을_응답한다() throws URISyntaxException, IOException {
        // given
        String fileUri = "/nextstep.txt";
        ResponseProcessor responseProcessor = new ResponseProcessor(fileUri);

        // when
        String response = responseProcessor.getResponse();

        // then
        String fileContent = "nextstep";
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + fileContent.getBytes().length + " ",
                "",
                fileContent);
        assertThat(response).isEqualTo(expected);
    }
}
