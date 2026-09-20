package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FormContentsTest {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    @Test
    void parseFormBody() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("account=gugu&password=secret"));

        // then
        assertThat(contents.find("account")).contains("gugu");
        assertThat(contents.find("password")).contains("secret");
    }

    @Test
    void decodePercentEncodedValue() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("email=hkkang%40woowahan.com"));

        // then
        assertThat(contents.find("email")).contains("hkkang@woowahan.com");
    }

    @Test
    void decodePlusAsSpace() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, body("name=hello+world"));

        // then
        assertThat(contents.find("name")).contains("hello world");
    }

    @Test
    void parseWhenContentTypeHasCharset() {
        // when
        final FormContents contents = FormContents.of(
                FORM_CONTENT_TYPE + "; charset=UTF-8", body("account=gugu"));

        // then
        assertThat(contents.find("account")).contains("gugu");
    }

    @Test
    void emptyWhenBodyIsNull() {
        // when
        final FormContents contents = FormContents.of(FORM_CONTENT_TYPE, null);

        // then
        assertThat(contents.find("account")).isEmpty();
    }

    @Test
    void emptyWhenContentTypeIsNotForm() {
        // when
        final FormContents contents = FormContents.of("application/json", body("account=gugu"));

        // then
        assertThat(contents.find("account")).isEmpty();
    }

    @Test
    void emptyWhenContentTypeIsMissing() {
        // when
        final FormContents contents = FormContents.of(null, body("account=gugu"));

        // then
        assertThat(contents.find("account")).isEmpty();
    }

    @Test
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
