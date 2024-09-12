package org.apache.catalina.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @Nested
    @DisplayName("header 파싱")
    class parseHeaders {
        @Test
        @DisplayName("성공 : 첫줄을 제외한 header 파싱 성공")
        void parseHeadersSuccess() {
            final List<String> httpRequest = List.of(
                    "GET / HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ");

            Map<String, String> headers = HttpRequestParser.parseHeaders(httpRequest);

            assertThat(headers).isEqualTo(Map.of("Host", "localhost:8080", "Connection", "keep-alive"));
        }
    }

    @Nested
    @DisplayName("파라미터 키-값 형식으로 파싱")
    class parseParamValues {
        @Test
        @DisplayName("성공 : 키-값 형식으로 파싱 성공")
        void parseParamValuesSuccess() {
            final List<String> param = List.of("account=gugu", "password=password");

            Map<String, String> actual = HttpRequestParser.parseParamValues(param);

            assertThat(actual).isEqualTo(Map.of("account", "gugu", "password", "password"));
        }
    }
}
