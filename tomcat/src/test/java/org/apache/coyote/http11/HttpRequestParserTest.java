package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.util.Symbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    private final HttpRequestParser parser = new HttpRequestParser();

    @DisplayName("Http Request 객체를 connection으로부터 추출한다.")
    @Test
    void extractRequest() throws IOException {
        // given
        String method = "GET";
        String path = "/index.html";
        String[] parameters = new String[0];
        String protocal = "HTTP/1.1";
        String requestBody = "";

        String host = "Host";
        String hostName = "localhost:8080";
        String headerHost = host + Symbol.COLON + hostName;
        String connection = "Connection";
        String connectionType = "keep-alive";
        String headerConnection = connection + Symbol.COLON + connectionType;
        String accept = "Accept";
        String acceptValue = "*/*";
        String headerAccept = accept + Symbol.COLON + acceptValue;
        String[] headers = {headerHost, headerConnection, headerAccept};

        HttpRequest expected = new HttpRequest(method, path, path, parameters, protocal, headers, requestBody);

        final String requestString = String.join(
                "\r\n",
                method + " " + path + " " + protocal + " ",
                headerHost,
                headerConnection,
                headerAccept,
                ""
        );
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());

        // when
        HttpRequest actualRequest = parser.extractRequest(inputStream);

        // then
        assertThat(actualRequest.toString()).isEqualTo(expected.toString());
    }
}
