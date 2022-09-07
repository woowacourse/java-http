package nextstep.jwp.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http.request.HttpRequest;

abstract class ControllerTest {

    protected HttpRequest getHttpRequest(final String method, final String path) throws IOException {
        final String httpMessage = String.join("\r\n",
                method + " " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpMessage.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return HttpRequest.from(bufferedReader);
    }
}
