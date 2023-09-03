package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.catalina.servlet.request.Body;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.StartLine;
import org.apache.coyote.http11.HttpRequestParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequestParser 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpRequestParserTest {

    @Test
    void body_없는_http_메세지를_파싱한다() {
        // given
        StringBuilder sb = new StringBuilder();
        sb.append("GET /index.html HTTP/1.1\r\n");
        sb.append("Host: localhost:8080\r\n");
        sb.append("User-Agent: Insomnia/2023.5.7\r\n");
        sb.append("Accept: */*\r\n");
        String requestString = sb.toString();
        StartLine expectedStartLine = StartLine.from("GET /index.html HTTP/1.1");
        Map<String, String> expectedHeaders = Map.of(
                "Host", "localhost:8080",
                "User-Agent", "Insomnia/2023.5.7",
                "Accept", "*/*"
        );
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(requestString.getBytes()))
        );

        // when
        HttpRequest request = HttpRequestParser.parse(br);

        // then
        assertThat(request.startLine())
                .usingRecursiveComparison()
                .isEqualTo(expectedStartLine);
        assertThat(request.headers().headers())
                .usingRecursiveComparison()
                .isEqualTo(expectedHeaders);
        assertThat(request.body()).isNull();
    }

    @Test
    void body_있는_http_메세지를_파싱한다() {
        // given
        StringBuilder sb = new StringBuilder();
        sb.append("POST /post HTTP/1.1\r\n");
        sb.append("Host: localhost:8080\r\n");
        sb.append("User-Agent: Insomnia/2023.5.7\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("Content-Length: 20\r\n");
        sb.append("\r\n");
        sb.append("{\n"
                + "\tname: \"mallang\"\n"
                + "}");
        String requestString = sb.toString();
        StartLine expectedStartLine = StartLine.from("POST /post HTTP/1.1");
        Map<String, String> expectedHeaders = Map.of(
                "Host", "localhost:8080",
                "User-Agent", "Insomnia/2023.5.7",
                "Accept", "*/*",
                "Content-Length", "20"
        );
        Body body = new Body("{\n"
                + "\tname: \"mallang\"\n"
                + "}");
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(requestString.getBytes()))
        );

        // when
        HttpRequest request = HttpRequestParser.parse(br);

        // then
        assertThat(request.startLine())
                .usingRecursiveComparison()
                .isEqualTo(expectedStartLine);
        assertThat(request.headers().headers())
                .usingRecursiveComparison()
                .isEqualTo(expectedHeaders);
        assertThat(request.body())
                .isEqualTo(body.body());
    }
}
