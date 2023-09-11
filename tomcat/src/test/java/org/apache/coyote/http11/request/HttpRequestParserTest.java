package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    private static final String inputWithoutBody = String.join(System.lineSeparator(),
            "GET /example?queryString=queryquery HTTP/1.1",
            "Host: www.example.com",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5",
            "Connection: keep-alive"
    );
    private static final String inputWithBody = String.join(System.lineSeparator(),
            "GET /example HTTP/1.1",
            "Host: www.example.com",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5",
            "Content-Type: multipart/form-data",
            "Content-Length: 30",
            "Connection: keep-alive",
            "",
            "account=gugu&password=password"
    );

    @Test
    @DisplayName("입력을 기반으로, HttpRequest를 생성할 수 있다.")
    void parseHttpRequestWithoutBody() {
        //given
        System.setIn(new ByteArrayInputStream(inputWithoutBody.getBytes()));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            //when
            HttpRequest httpRequest = HttpRequestParser.parse(br);
            HttpRequestHeaders requestHeaders = httpRequest.getHttpRequestHeaders();

            //then
            assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
            assertThat(httpRequest.getNativePath()).isEqualTo("/example");
            assertThat(httpRequest.getQueryString().get("queryString")).isEqualTo("queryquery");
            assertThat(requestHeaders.get("Host")).isEqualTo("www.example.com");
            assertThat(requestHeaders.get("Accept"))
                    .isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            assertThat(requestHeaders.hasCookie()).isFalse();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    @DisplayName("입력을 기반으로, HttpRequest를 생성할 수 있다.")
    void parseHttpRequestWithBody() {
        //given
        System.setIn(new ByteArrayInputStream(inputWithBody.getBytes()));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            //when
            HttpRequest httpRequest = HttpRequestParser.parse(br);
            HttpRequestHeaders requestHeaders = httpRequest.getHttpRequestHeaders();

            //then
            assertThat(httpRequest.getHttpMethod()).isEqualTo(HttpMethod.GET);
            assertThat(httpRequest.getNativePath()).isEqualTo("/example");
            assertThat(requestHeaders.get("Host")).isEqualTo("www.example.com");
            assertThat(requestHeaders.get("Accept"))
                    .isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            assertThat(requestHeaders.hasCookie()).isFalse();
            assertThat(httpRequest.getHttpBody().getBody()).isEqualTo("account=gugu&password=password");
        } catch (IOException e) {
            fail();
        }

    }
}