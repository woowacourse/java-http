package org.apache.coyote.handle.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HelloWorldHandlerTest {

    @Test
    void GET_요청시_HELLO_WORLD를_응답한다() throws Exception {
        final String httpRequestMessage = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 12",
                "Accept: */*;q=0.1, text/html;q=0.8, application/json;q=0.5"
        );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_11);

        final HelloWorldHandler helloWorldHandler = new HelloWorldHandler();
        helloWorldHandler.doGet(httpRequest, httpResponse);

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
        assertThat(httpResponse).hasToString(expected);
    }
}
