package org.apache.catalina.domain.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestBodyTest {

    @DisplayName("기본 폼 데이터 파싱이 정상적으로 동작한다")
    @Test
    void parseFormData_basic() {
        // given
        String content = "name=john&age=25&city=seoul";
        HttpRequestBody body = new HttpRequestBody(content);

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result)
                .hasSize(3)
                .containsEntry("name", "john")
                .containsEntry("age", "25")
                .containsEntry("city", "seoul");
    }

    @DisplayName("URL 인코딩된 데이터를 올바르게 디코딩한다")
    @Test
    void parseFormData_urlEncoded() {
        // given
        String content = "name=John+Doe&email=john%40example.com&message=Hello%20World%21";
        HttpRequestBody body = new HttpRequestBody(content);

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result)
                .hasSize(3)
                .containsEntry("name", "John Doe")
                .containsEntry("email", "john@example.com")
                .containsEntry("message", "Hello World!");
    }

    @DisplayName("빈 값이 있는 파라미터를 처리한다")
    @Test
    void parseFormData_emptyValue() {
        // given
        String content = "username=admin&password=&remember=on";
        HttpRequestBody body = new HttpRequestBody(content);

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result)
                .hasSize(3)
                .containsEntry("username", "admin")
                .containsEntry("password", "")
                .containsEntry("remember", "on");
    }

    @DisplayName("값이 없는 키만 있는 경우를 처리한다")
    @Test
    void parseFormData_keyOnly() {
        // given
        String content = "checkbox1&checkbox2=value&checkbox3";
        HttpRequestBody body = new HttpRequestBody(content);

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result)
                .hasSize(3)
                .containsEntry("checkbox1", "")
                .containsEntry("checkbox2", "value")
                .containsEntry("checkbox3", "");
    }

    @DisplayName("빈 문자열 content를 처리한다")
    @Test
    void parseFormData_emptyContent() {
        // given
        HttpRequestBody body = new HttpRequestBody("");

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("null content를 처리한다")
    @Test
    void parseFormData_nullContent() {
        // given
        HttpRequestBody body = new HttpRequestBody(null);

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("단일 파라미터를 처리한다")
    @Test
    void parseFormData_singleParameter() {
        // given
        HttpRequestBody body = new HttpRequestBody("token=abc123");

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result)
                .hasSize(1)
                .containsEntry("token", "abc123");
    }

    @DisplayName("특수 문자가 포함된 값을 처리한다")
    @Test
    void parseFormData_specialCharacters() {
        // given
        String content = "data=%7B%22key%22%3A%22value%22%7D&symbol=%26%3D%23";
        HttpRequestBody body = new HttpRequestBody(content);

        // when
        Map<String, String> result = body.parseFormData();

        // then
        assertThat(result)
                .hasSize(2)
                .containsEntry("data", "{\"key\":\"value\"}")
                .containsEntry("symbol", "&=#");
    }
}
