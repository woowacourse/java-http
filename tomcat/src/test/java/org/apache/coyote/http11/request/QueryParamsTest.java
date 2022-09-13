package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.TestMessage.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    @DisplayName("QueryString으로 주어진 경우 Value를 조회한다.")
    void getQueryValue_queryString() throws URISyntaxException {
        // given
        final HttpMessage message = generateMessageQueryString("query1=value1&query2=value2");
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
        final HttpMessage message = generateMessageWithMessageBody("query1=value1&query2=value2");
        final URI requestUri = new URI("/path");
        final QueryParams queryParams = new QueryParams(requestUri, message);

        // when
        final String actual = queryParams.getQueryValue("query2");
        final String expected = "value2";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
