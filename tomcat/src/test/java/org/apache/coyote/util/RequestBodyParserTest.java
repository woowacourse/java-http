package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.request.HttpRequestBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyParserTest {

    @DisplayName("요청 Body의 하나의 key value로 이뤄진 쿼리파라미터를 파싱한다.")
    @Test
    void parseFormData() {
        HttpRequestBody httpRequestBody = new HttpRequestBody("key1=value1");

        Map<String, String> result = RequestBodyParser.parseFormData(httpRequestBody);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsEntry("key1", "value1")
        );
    }

    @DisplayName("요청 Body의 복수의 쿼리파라미터로 이뤄진 문자열을 파싱한다.")
    @Test
    void parseFormDataMultiple() {
        HttpRequestBody httpRequestBody = new HttpRequestBody("key1=value1&key2=value2");

        Map<String, String> result = RequestBodyParser.parseFormData(httpRequestBody);

        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result).containsEntry("key1", "value1"),
                () -> assertThat(result).containsEntry("key2", "value2")
        );
    }

    @DisplayName("요청 Body의 동일한 키를 가진 쿼리파라미터가 있을 경우 마지막 값으로 파싱한다.")
    @Test
    void parseFormDataDuplicated() {
        HttpRequestBody httpRequestBody = new HttpRequestBody("key1=value1&key1=value2");

        Map<String, String> result = RequestBodyParser.parseFormData(httpRequestBody);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsEntry("key1", "value2")
        );
    }

    @DisplayName("요청 Body가 비어있으면 빈 Map을 반환한다.")
    @Test
    void parseFormDataEmpty() {
        HttpRequestBody httpRequestBody = new HttpRequestBody("");

        Map<String, String> result = RequestBodyParser.parseFormData(httpRequestBody);

        assertThat(result).hasSize(0);
    }

    @DisplayName("key는 있지만 value가 없는 쿼리파라미터를 파싱한다.")
    @Test
    void parseFormDataNoValue() {
        HttpRequestBody httpRequestBody = new HttpRequestBody("key1=");

        Map<String, String> result = RequestBodyParser.parseFormData(httpRequestBody);

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).containsEntry("key1", "")
        );
    }

    @DisplayName("value가 없는 복수의 쿼리파라미터로 이뤄진 문자열을 파싱한다.")
    @Test
    void parseFormDataNoValues() {
        HttpRequestBody httpRequestBody = new HttpRequestBody("key1=&key2=");

        Map<String, String> result = RequestBodyParser.parseFormData(httpRequestBody);

        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result).containsEntry("key1", ""),
                () -> assertThat(result).containsEntry("key2", "")

        );
    }
}
