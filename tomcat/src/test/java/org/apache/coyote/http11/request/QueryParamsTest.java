package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QueryParamsTest {

    @Test
    @DisplayName("QueryString으로 주어진 경우 Value를 조회한다.")
    void getQueryValue_queryString() throws URISyntaxException {
        // given
        final HttpMessage message = generateMessageQueryString();
        final URI requestUri = new URI("/path?query1=value1&query2=value2");
        final QueryParams queryParams = new QueryParams(requestUri, message);

        // when
        final String actual = queryParams.getQueryValue("query2");
        final String expected = "value2";

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("MessageBody으로 주어진 경우 Value를 조회한다.")
    void getQueryValue_messageBody() throws URISyntaxException {
        // given
        final HttpMessage message = generateMessageWithMessageBody();
        final URI requestUri = new URI("/path");
        final QueryParams queryParams = new QueryParams(requestUri, message);

        // when
        final String actual = queryParams.getQueryValue("query2");
        final String expected = "value2";

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private HttpMessage generateMessageQueryString() {
        final String httpRequest = String.join("\r\n",
            "GET /path?query1=value1&query2=value2 HTTP/1.1",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "", "");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        return new HttpMessage(inputStream);
    }

    private HttpMessage generateMessageWithMessageBody() {
        final String httpRequest = String.join("\r\n",
            "GET /path HTTP/1.1",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 27",
            "Content-Type: application/x-www-form-urlencoded",
            "",
            "query1=value1&query2=value2");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        return new HttpMessage(inputStream);
    }
}
