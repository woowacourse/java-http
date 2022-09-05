package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.model.HttpMethod;
import org.apache.coyote.http11.request.model.HttpRequestStartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestParserTest {

    @Test
    @DisplayName("http 요청의 첫 줄을 가져온다.")
    void getStartLine() {
        String request = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*";
        List<String[]> requests = toSplit(request.split("\n"));

        HttpRequestParser httpRequestUtils = new HttpRequestParser(requests);
        HttpRequestStartLine startLine = httpRequestUtils.getHttpRequestStartLine();

        assertThat(startLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(startLine.getUri().getValue()).isEqualTo("/index.html");
    }

    @Test
    @DisplayName("http 요청의 헤더들을 가져온다.")
    void getHeaders() {
        String request = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Accept: */*";
        List<String[]> requests = toSplit(request.split("\n"));

        HttpRequestParser httpRequestUtils = new HttpRequestParser(requests);
        HttpHeaders httpHeaders = httpRequestUtils.getHeaders();

        assertThat(httpHeaders.getValue("Host")).isEqualTo("localhost:8080");
        assertThat(httpHeaders.getValue("Accept")).isEqualTo("*/*");
    }

    private List<String[]> toSplit(final String[] request) {
        return Arrays.stream(request)
                .map(it -> it.split(" "))
                .collect(Collectors.toList());
    }
}
