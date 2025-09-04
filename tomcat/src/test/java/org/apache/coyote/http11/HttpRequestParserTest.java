package org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.catalina.domain.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Test
    void 단순한_GET_요청_파싱() throws IOException {
        // given
        String httpRequest = """
                GET /index.html HTTP/1.1
                Host: localhost:8080
                User-Agent: Mozilla/5.0
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("GET", result.startLine().method());
        assertEquals("/index.html", result.startLine().path());
        assertEquals("HTTP/1.1", result.startLine().version());
        assertEquals("localhost:8080", result.headers().get("Host"));
        assertEquals("Mozilla/5.0", result.headers().get("User-Agent"));
        assertTrue(result.queryStrings().isEmpty());
    }

    @Test
    void 쿼리_파라미터가_있는_GET_요청_파싱() throws IOException {
        // given
        String httpRequest = """
                GET /login?account=admin&password=123 HTTP/1.1
                Host: localhost:8080
                Content-Type: text/html
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("GET", result.startLine().method());
        assertEquals("/login", result.startLine().path());
        assertEquals("HTTP/1.1", result.startLine().version());
        assertEquals("admin", result.queryStrings().get("account"));
        assertEquals("123", result.queryStrings().get("password"));
        assertEquals("localhost:8080", result.headers().get("Host"));
        assertEquals("text/html", result.headers().get("Content-Type"));
    }

    @Test
    void POST_요청_파싱() throws IOException {
        // given
        String httpRequest = """
                POST /api/users HTTP/1.1
                Host: localhost:8080
                Content-Type: application/json
                Content-Length: 25
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("POST", result.startLine().method());
        assertEquals("/api/users", result.startLine().path());
        assertEquals("HTTP/1.1", result.startLine().version());
        assertEquals("localhost:8080", result.headers().get("Host"));
        assertEquals("application/json", result.headers().get("Content-Type"));
        assertEquals("25", result.headers().get("Content-Length"));
        assertTrue(result.queryStrings().isEmpty());
    }

    @Test
    void 헤더가_없는_요청_파싱() throws IOException {
        // given
        String httpRequest = """
                GET /simple HTTP/1.1
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("GET", result.startLine().method());
        assertEquals("/simple", result.startLine().path());
        assertEquals("HTTP/1.1", result.startLine().version());
        assertTrue(result.headers().isEmpty());
        assertTrue(result.queryStrings().isEmpty());
    }

    @Test
    void 여러_쿼리_파라미터가_있는_요청_파싱() throws IOException {
        // given
        String httpRequest = """
                GET /search?q=java&page=1&size=10 HTTP/1.1
                Host: localhost:8080
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("GET", result.startLine().method());
        assertEquals("/search", result.startLine().path());
        assertEquals("java", result.queryStrings().get("q"));
        assertEquals("1", result.queryStrings().get("page"));
        assertEquals("10", result.queryStrings().get("size"));
    }

    @Test
    void null_요청라인_예외_발생() {
        // given
        BufferedReader reader = new BufferedReader(new StringReader(""));

        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> HttpRequestParser.parse(reader)
        );
    }

    @Test
    void 쿼리_파라미터가_없는_요청() throws IOException {
        // given
        String httpRequest = """
                GET /home HTTP/1.1
                Host: localhost:8080
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertTrue(result.queryStrings().isEmpty());
        assertEquals("/home", result.startLine().path());
    }

    @Test
    void 잘못된_형식의_헤더_무시() throws IOException {
        // given
        String httpRequest = """
                GET /test HTTP/1.1
                Host: localhost:8080
                InvalidHeader
                Content-Type: text/html
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("localhost:8080", result.headers().get("Host"));
        assertEquals("text/html", result.headers().get("Content-Type"));
        assertFalse(result.headers().containsKey("InvalidHeader"));
    }

    @Test
    void 잘못된_형식의_쿼리_파라미터_무시() throws IOException {
        // given
        String httpRequest = """
                GET /test?valid=value&invalid&another=value2 HTTP/1.1
                Host: localhost:8080
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertEquals("value", result.queryStrings().get("valid"));
        assertEquals("value2", result.queryStrings().get("another"));
        assertFalse(result.queryStrings().containsKey("invalid"));
    }
}
