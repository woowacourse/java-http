package org.apache.coyote.http11.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.request.converter.HttpRequestConverter;
import org.apache.coyote.http11.request.domain.RequestLine;
import org.apache.coyote.http11.request.domain.RequestMethod;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestConverterTest {

    @Test
    @DisplayName("요청을 HttpRequest로 만든다.")
    void convertFrom() throws IOException {
        String inputRequestLine = """
                POST /register HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                Content-Length: 80
                Content-Type: application/x-www-form-urlencoded
                Accept: */*
                                
                account=gugu&password=password&email=hkkang@woowahan.com""";

        final InputStream inputStream = new ByteArrayInputStream(inputRequestLine.getBytes());
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        HttpRequest httpRequest = HttpRequestConverter.convertFrom(bufferedReader);
        RequestLine requestLine = httpRequest.getRequestLine();

        assertAll(
                () -> assertEquals(requestLine.getPathValue(), "/register"),
                () -> assertTrue(requestLine.isSameMethod(RequestMethod.POST)),
                () -> assertEquals(httpRequest.getHeader().getBodyLength(), 80),
                () -> assertEquals(httpRequest.getRequestBody().getBody(),
                        "account=gugu&password=password&email=hkkang@woowahan.com")
        );
    }

}
