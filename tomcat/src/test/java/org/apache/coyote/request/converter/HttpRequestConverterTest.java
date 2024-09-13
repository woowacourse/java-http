package org.apache.coyote.request.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestConverterTest {

    @Test
    @DisplayName("요청을 HttpRequest로 만든다.")
    void convertFrom() throws IOException {
        String inputRequestLine = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang@woowahan.com");

        final InputStream inputStream = new ByteArrayInputStream(inputRequestLine.getBytes());
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        HttpRequest httpRequest = HttpRequestConverter.convertFrom(bufferedReader);
        HttpRequest expectedRequest = HttpRequestFixture.POST_REGISTER_PATH_REQUEST;

        assertEquals(expectedRequest, httpRequest);
    }

}
