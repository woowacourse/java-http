package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.http.common.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestBodyTest {

    @Test
    @DisplayName("form-urlencoded 바디 파싱")
    void parseFormUrlencodedBody() {
        // given
        final String rawBody = "account=user&password=1234&email=user%40example.com";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.getValue("account")).isEqualTo("user");
            softly.assertThat(body.getValue("password")).isEqualTo("1234");
            softly.assertThat(body.getValue("email")).isEqualTo("user@example.com");
        });
    }

    @Test
    @DisplayName("URL 디코딩이 포함된 바디 파싱")
    void parseBodyWithUrlDecoding() {
        // given
        final String rawBody = "name=John%20Doe&message=Hello%20World%21&email=test%40example.com";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.getValue("name")).isEqualTo("John Doe");
            softly.assertThat(body.getValue("message")).isEqualTo("Hello World!");
            softly.assertThat(body.getValue("email")).isEqualTo("test@example.com");
        });
    }

    @Test
    @DisplayName("빈 바디")
    void parseEmptyBody() {
        // given
        final String rawBody = "";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertThat(body.getValue("nonexistent")).isEmpty();
    }

    @Test
    @DisplayName("null 바디")
    void parseNullBody() {
        // when
        final HttpRequestBody body = HttpRequestBody.from(null, ContentType.FORM_URLENCODED);

        // then
        assertThat(body.getValue("nonexistent")).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 파라미터")
    void getNonexistentParameter() {
        // given
        final String rawBody = "account=user&password=1234";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertThat(body.getValue("nonexistent")).isEmpty();
    }

    @Test
    @DisplayName("빈 값을 가진 파라미터")
    void parseParameterWithEmptyValue() {
        // given
        final String rawBody = "account=user&password=&email=test@example.com";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.getValue("account")).isEqualTo("user");
            softly.assertThat(body.getValue("password")).isEmpty();
            softly.assertThat(body.getValue("email")).isEqualTo("test@example.com");
        });
    }

    @Test
    @DisplayName("잘못된 형식의 파라미터 (등호 없음)")
    void parseInvalidParameterFormat() {
        // given
        final String rawBody = "account=user&invalidparam&password=1234";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.getValue("account")).isEqualTo("user");
            softly.assertThat(body.getValue("password")).isEqualTo("1234");
            softly.assertThat(body.getValue("invalidparam")).isEmpty();
        });
    }

    @Test
    @DisplayName("단일 파라미터")
    void parseSingleParameter() {
        // given
        final String rawBody = "token=abc123";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);

        // then
        assertThat(body.getValue("token")).isEqualTo("abc123");
    }

    @Test
    @DisplayName("HTML content type으로 바디 파싱 (파싱하지 않음)")
    void parseBodyWithHtmlContentType() {
        // given
        final String rawBody = "account=user&password=1234";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.HTML);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.getValue("account")).isEmpty();
            softly.assertThat(body.getValue("password")).isEmpty();
        });
    }

    @Test
    @DisplayName("바디 toString")
    void bodyToString() {
        // given
        final String rawBody = "account=user&password=1234&email=test@example.com";

        // when
        final HttpRequestBody body = HttpRequestBody.from(rawBody, ContentType.FORM_URLENCODED);
        final String result = body.toString();

        // then
        assertThat(result).contains("account=user");
        assertThat(result).contains("password=1234");
        assertThat(result).contains("email=test@example.com");
    }

    @Test
    @DisplayName("빈 바디 toString")
    void emptyBodyToString() {
        // when
        final HttpRequestBody body = HttpRequestBody.empty();
        final String result = body.toString();

        // then
        assertThat(result).isEmpty();
    }
}