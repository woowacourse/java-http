package coyote.http;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestParserTest {

    @Test
    void convertToHttpRequest() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();

        //when
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        //then
       assertAll(
                () -> assertEquals("GET /index.html HTTP/1.1", httpRequest.getMethod() + " " + httpRequest.getPath() + " " + httpRequest.getProtocol().getName()),
                () -> assertEquals("header", httpRequest.getHeader().get("header")),
                () -> assertEquals("test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46", httpRequest.getHeader().get("Cookie")),
                () -> assertEquals("message body", httpRequest.getMessageBody())
        );
    }
}
