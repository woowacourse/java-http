package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.spring.http.enums.HttpMethod;
import com.techcourse.exception.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import com.spring.http.request.HttpRequest;
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
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.GET);
        assertThat(result.requestStartLine().path()).isEqualTo("/index.html");
        assertThat(result.requestStartLine().version()).isEqualTo("HTTP/1.1");
        assertThat(result.header().get("Host")).isEqualTo("localhost:8080");
        assertThat(result.header().get("User-Agent")).isEqualTo("Mozilla/5.0");
        assertThat(result.queryStrings()).isEmpty();
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
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.GET);
        assertThat(result.requestStartLine().path()).isEqualTo("/login");
        assertThat(result.requestStartLine().version()).isEqualTo("HTTP/1.1");
        assertThat(result.queryStrings()).containsEntry("account", "admin");
        assertThat(result.queryStrings()).containsEntry("password", "123");
        assertThat(result.header().get("Host")).isEqualTo("localhost:8080");
        assertThat(result.header().get("Content-Type")).isEqualTo("text/html");
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
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.POST);
        assertThat(result.requestStartLine().path()).isEqualTo("/api/users");
        assertThat(result.requestStartLine().version()).isEqualTo("HTTP/1.1");
        assertThat(result.header().get("Host")).isEqualTo("localhost:8080");
        assertThat(result.header().get("Content-Type")).isEqualTo("application/json");
        assertThat(result.header().get("Content-Length")).isEqualTo("25");
        assertThat(result.queryStrings()).isEmpty();
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
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.GET);
        assertThat(result.requestStartLine().path()).isEqualTo("/simple");
        assertThat(result.requestStartLine().version()).isEqualTo("HTTP/1.1");
        assertThat(result.header().get("Host")).isNull();
        assertThat(result.queryStrings()).isEmpty();
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
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.GET);
        assertThat(result.requestStartLine().path()).isEqualTo("/search");
        assertThat(result.queryStrings()).containsEntry("q", "java");
        assertThat(result.queryStrings()).containsEntry("page", "1");
        assertThat(result.queryStrings()).containsEntry("size", "10");
    }

    @Test
    void null_요청라인_예외_발생() {
        // given
        BufferedReader reader = new BufferedReader(new StringReader(""));

        // when & then
        assertThatThrownBy(() -> HttpRequestParser.parse(reader))
                .isInstanceOf(BadRequestException.class);
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
        assertThat(result.queryStrings()).isEmpty();
        assertThat(result.requestStartLine().path()).isEqualTo("/home");
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
        assertThat(result.header().get("Host")).isEqualTo("localhost:8080");
        assertThat(result.header().get("Content-Type")).isEqualTo("text/html");
        assertThat(result.header().get("InvalidHeader")).isNull();
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
        assertThat(result.queryStrings()).containsEntry("valid", "value");
        assertThat(result.queryStrings()).containsEntry("another", "value2");
        assertThat(result.queryStrings()).doesNotContainKey("invalid");
    }

    @Test
    void POST_요청_body_파싱() throws IOException {
        // given
        String httpRequest = """
                POST /api/users HTTP/1.1
                Host: localhost:8080
                Content-Type: application/json
                Content-Length: 24
                
                {"name":"john","age":25}""";
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.POST);
        assertThat(result.requestStartLine().path()).isEqualTo("/api/users");
        assertThat(result.header().get("Content-Type")).isEqualTo("application/json");
        assertThat(result.body().content()).isEqualTo("{\"name\":\"john\",\"age\":25}");
    }

    @Test
    void POST_요청_form_data_body_파싱() throws IOException {
        // given
        String httpRequest = """
                POST /login HTTP/1.1
                Host: localhost:8080
                Content-Type: application/x-www-form-urlencoded
                Content-Length: 26
                
                account=admin&password=123""";
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.POST);
        assertThat(result.requestStartLine().path()).isEqualTo("/login");
        assertThat(result.header().get("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(result.body().content()).isEqualTo("account=admin&password=123");
    }

    @Test
    void Content_Length가_0인_POST_요청_파싱() throws IOException {
        // given
        String httpRequest = """
                POST /api/ping HTTP/1.1
                Host: localhost:8080
                Content-Type: application/json
                Content-Length: 0
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.POST);
        assertThat(result.requestStartLine().path()).isEqualTo("/api/ping");
        assertThat(result.header().get("Content-Length")).isEqualTo("0");
        assertThat(result.body().content()).isEmpty();
    }

    @Test
    void Content_Length가_없는_GET_요청_body_파싱() throws IOException {
        // given
        String httpRequest = """
                GET /api/users HTTP/1.1
                Host: localhost:8080
                
                """;
        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        // when
        HttpRequest result = HttpRequestParser.parse(reader);

        // then
        assertThat(result.requestStartLine().method()).isEqualTo(HttpMethod.GET);
        assertThat(result.requestStartLine().path()).isEqualTo("/api/users");
        assertThat(result.body().content()).isEmpty();
    }
}
