package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("폼 본문 파싱")
class FormContentsTest {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    @Test
    @DisplayName("폼 본문을 키와 값으로 파싱한다")
    void parseFormBody() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("account=gugu&password=secret"));

        // then
        assertThat(contents.find("account")).contains("gugu");
        assertThat(contents.find("password")).contains("secret");
    }

    @Test
    @DisplayName("퍼센트 인코딩된 값을 디코딩한다")
    void decodePercentEncodedValue() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("email=hkkang%40woowahan.com"));

        // then
        assertThat(contents.find("email")).contains("hkkang@woowahan.com");
    }

    @Test
    @DisplayName("더하기 기호를 공백으로 디코딩한다")
    void decodePlusAsSpace() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("name=hello+world"));

        // then
        assertThat(contents.find("name")).contains("hello world");
    }

    @Test
    @DisplayName("Content-Type에 charset이 붙어 있어도 파싱한다")
    void parseWhenContentTypeHasCharset() {
        // when
        final FormContents contents = FormContents.of(
                FORM_CONTENT_TYPE + "; charset=UTF-8", body("account=gugu"));

        // then
        assertThat(contents.find("account")).contains("gugu");
    }

    @Test
    @DisplayName("본문이 없으면 빈 값을 반환한다")
    void emptyWhenBodyIsNull() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, null);

        // then
        assertThat(contents.find("account")).isEmpty();
    }

    @Test
    @DisplayName("폼이 아닌 Content-Type이면 파싱하지 않는다")
    void emptyWhenContentTypeIsNotForm() {
        // when
        final FormContents contents = FormContents.of("application/json", body("account=gugu"));

        // then
        assertThat(contents.find("account")).isEmpty();
    }

    @Test
    @DisplayName("Content-Type이 없으면 파싱하지 않는다")
    void emptyWhenContentTypeIsMissing() {
        // when
        final FormContents contents = FormContents.of(null, body("account=gugu"));

        // then
        assertThat(contents.find("account")).isEmpty();
    }

    @Test
    @DisplayName("형식이 어긋난 구간은 건너뛴다")
    void skipMalformedSegment() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("account=gugu&broken&password=secret"));

        // then
        assertThat(contents.find("account")).contains("gugu");
        assertThat(contents.find("password")).contains("secret");
        assertThat(contents.find("broken")).isEmpty();
    }

    private byte[] body(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
