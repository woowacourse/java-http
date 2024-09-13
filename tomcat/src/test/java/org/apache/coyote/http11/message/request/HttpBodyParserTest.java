package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpBodyParserTest {

    @Test
    @DisplayName("application/x-www-form-urlencoded 타입의 body를 FormParameters로 변환한다.")
    void parseToFormParametersTest() {
        // given
        String body = "key1=value1&key2=value2";
        HttpHeaders headers = new HttpHeaders();
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // when
        FormParameters formParameters = HttpBodyParser.parseToFormParameters(body.getBytes(), headers);

        // then
        assertEquals("value1", formParameters.getSingleValueByKey("key1"));
        assertEquals("value2", formParameters.getSingleValueByKey("key2"));
    }

    @Test
    @DisplayName("application/x-www-form-urlencoded 타입이 아닌 body는 빈 FormParameters로 변환한다.")
    void parseToFormParametersWithInvalidContentTypeTest() {
        // given
        String body = "key1=value1&key2=value2";
        HttpHeaders headers = new HttpHeaders();
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "application/json");

        // when
        FormParameters formParameters = HttpBodyParser.parseToFormParameters(body.getBytes(), headers);

        // then
        assertThat(formParameters.hasParameters()).isFalse();
    }
}
