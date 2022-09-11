package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void createResponse() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.addStatusLine(HttpStatus.OK.getStatusCodeAndMessage());
        httpResponse.addContentTypeHeader(ContentType.findContentType("/index.html"));
        httpResponse.addBodyFromFile("/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String response = httpResponse.makeResponse();
        assertThat(response).isEqualTo(expected);
    }

    @Test
    void addHeader() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.addStatusLine(HttpStatus.OK.getStatusCodeAndMessage());
        httpResponse.addContentTypeHeader(ContentType.findContentType("/index.html"));
        httpResponse.addBodyFromFile("/index.html");

        httpResponse.addHeader(
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        String response = httpResponse.makeResponse();
        assertThat(response).contains("Cookie");
    }
}
