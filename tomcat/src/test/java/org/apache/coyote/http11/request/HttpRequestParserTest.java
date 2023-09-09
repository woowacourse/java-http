package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class HttpRequestParserTest {

    @DisplayName("request string을 HttpRequest 객체로 파싱한다.")
    @Test
    void parse() {
        // given
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: en-US,en;q=0.9",
                "Connection: keep-alive",
                "Host: localhost:8080",
                "Referer: http://localhost:8080/index.html",
                "");

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "localhost:8080");
        headers.put("Referer", "http://localhost:8080/index.html");

        // when
        BufferedReader reader = new BufferedReader(new StringReader(request));
        HttpRequestParser parser = new HttpRequestParser(reader);
        HttpRequest httpRequest = parser.parse();

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpRequest).extracting("requestLine")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpRequestLine.from("GET", "/index.html", "HTTP/1.1"));
                    softly.assertThat(httpRequest).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                    softly.assertThat(httpRequest).extracting("body")
                            .usingRecursiveComparison()
                            .isEqualTo(new HashMap<>());
                }
        );
    }

    @DisplayName("(queryString) request string을 HttpRequest 객체로 파싱한다.")
    @Test
    void parse_queryString() {
        // given
        String request = String.join("\r\n",
                "GET /?key1=1&key2=2 HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: en-US,en;q=0.9",
                "Connection: keep-alive",
                "Host: localhost:8080",
                "");

        Map<String, String> query = new HashMap<>();
        query.put("key1", "1");
        query.put("key2", "2");

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "localhost:8080");

        // when
        BufferedReader reader = new BufferedReader(new StringReader(request));
        HttpRequestParser parser = new HttpRequestParser(reader);
        HttpRequest httpRequest = parser.parse();

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpRequest).extracting("requestLine")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpRequestLine.from("GET", "/", query,"HTTP/1.1"));
                    softly.assertThat(httpRequest).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                    softly.assertThat(httpRequest).extracting("body")
                            .usingRecursiveComparison()
                            .isEqualTo(new HashMap<>());
                }
        );
    }

    @DisplayName("(body) request string을 HttpRequest 객체로 파싱한다.")
    @Test
    void parse_body() {
        // given
        String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                "Accept-Encoding: gzip, deflate, br",
                "Accept-Language: en-US,en;q=0.9",
                "Connection: keep-alive",
                "Host: localhost:8080",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password");

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "localhost:8080");
        headers.put("Content-Length", "30");
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");

        // when
        BufferedReader reader = new BufferedReader(new StringReader(request));
        HttpRequestParser parser = new HttpRequestParser(reader);
        HttpRequest httpRequest = parser.parse();

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(httpRequest).extracting("requestLine")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpRequestLine.from("POST", "/login","HTTP/1.1"));
                    softly.assertThat(httpRequest).extracting("headers")
                            .usingRecursiveComparison()
                            .isEqualTo(HttpHeaders.of(headers));
                    softly.assertThat(httpRequest).extracting("body")
                            .usingRecursiveComparison()
                            .isEqualTo(body);
                }
        );
    }
}
