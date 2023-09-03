package org.apache.coyote.http11.httpmessage.support;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Test
    @DisplayName("httpRequest가 들어올 때 httpMethod를 반환한다.")
    void get_Http_method_from_request() {
        // given
        final String request = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*");

        // when
        final HttpMethod result = HttpRequestParser.getHttpMethod(request);

        // then
        final HttpMethod expect = HttpMethod.GET;

        assertThat(result).isEqualTo(expect);
    }

    @Test
    @DisplayName("httpRequest가 들어올 때 path를 반환한다.")
    void get_path_from_request() {
        // given
        final String request = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*");

        // when
        final String result = HttpRequestParser.getPath(request);

        // then
        final String expect = "/index.html";

        assertThat(result).isEqualTo(expect);
    }

    @Test
    @DisplayName("httpRequest가 들어올 때 protocol을 반환한다.")
    void get_protocol_from_request() {
        // given
        final String request = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*");

        // when
        final String result = HttpRequestParser.getProtocol(request);

        // then
        final String expect = "HTTP/1.1";

        assertThat(result).isEqualTo(expect);
    }

    @Test
    @DisplayName("httpRequest가 들어올 때 header를 반환한다.")
    void get_header_from_request() {
        // given
        final String request = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*");

        // when
        final HttpHeader result = HttpRequestParser.getHeader(request);

        // then
        final Map<String, String> header = result.getHeader();

        assertThat(header.size()).isEqualTo(3);
        assertThat(header.get("Host")).isEqualTo("localhost:8080");
        assertThat(header.get("Connection")).isEqualTo("keep-alive");
        assertThat(header.get("Accept")).isEqualTo("*/*");

    }

    @Test
    @DisplayName("httpRequest가 들어올 때 queryString을 반환한다.")
    void get_query_string_from_request() {
        // given
        final String request = String.join("\r\n",
            "GET /login.html?account=ako HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*");

        // when
        final QueryString result = HttpRequestParser.getQueryString(request);

        // then
        final Map<String, String> queryString = result.getQueryString();

        assertThat(queryString.size()).isEqualTo(1);
        assertThat(queryString.get("account")).isEqualTo("ako");
    }

    @Test
    @DisplayName("httpRequest가 들어올 때 queryString이 없으면 null을 반환한다.")
    void get_no_query_string_from_request() {
        // given
        final String request = String.join("\r\n",
            "GET /login.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*");

        // when
        final QueryString result = HttpRequestParser.getQueryString(request);

        // then
        assertThat(result).isNull();
    }
}
