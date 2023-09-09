package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterHandlerTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    private final RegisterHandler registerHandler = new RegisterHandler();

    @Test
    void GET_요청_시_registerHTML반환() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        String fileData = extractFileData("/register.html");
        BufferedReader request = RequestParser.requestToInput(httpRequest);

        // when
        HttpResponse httpResponse = HttpResponse.create();
        registerHandler.handle(HttpRequest.from(request), httpResponse);
        String response = httpResponse.generateResponseMessage();

        String[] responseElements = response.split("\r\n");
        String responseBody = responseElements[responseElements.length - 1];

        // then
        assertThat(responseBody).isEqualTo(fileData);
    }

    @Test
    void POST_요청_시_indexHTML로_리다이렉션() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");
        BufferedReader request = RequestParser.requestToInput(httpRequest);

        // when
        HttpResponse httpResponse = HttpResponse.create();
        registerHandler.handle(HttpRequest.from(request), httpResponse);
        String response = httpResponse.generateResponseMessage();

        // then
        assertThat(response).contains(
                "Location: index.html",
                "HTTP/1.1 302 "
        );
    }

    private String extractFileData(String filePath) throws IOException {
        URL resource = classLoader.getResource("static" + filePath);
        File file = new File(resource.getFile());
        String fileData = new String(Files.readAllBytes(file.toPath()));
        return fileData;
    }
}
