package org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UriTest {

    @Test
    void isCallApi() {
        // given
        Uri uri = new Uri("/login");

        // when & then
        assertThat(uri.isCallApi()).isTrue();
    }

    @Test
    void isNotCallApi() {
        // given
        Uri uri = new Uri("/login.html");

        // when & then
        assertThat(uri.isCallApi()).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {"/styles.css, CSS", "/index.html, HTML", "/js/scripts.js, JS", "/favicon.ico, ICO"})
    void findContentType(String path, ContentType expected) {
        // given
        Uri uri = new Uri(path);

        // when & then
        assertThat(uri.findContentType()).isEqualTo(expected);
    }

    @Test
    void findFilePath() {
        // given
        Uri uri = new Uri("/index.html?key=value");

        // when & then
        assertThat(uri.findFilePath()).isEqualTo(FilePath.INDEX_PAGE);
    }

    @Test
    void getQueryString() {
        // given
        String key = "key";
        String value = "value";
        Uri uri = new Uri("/index.html?" + key + "=" + value);

        // when
        Map<String, String> result = uri.getQueryString();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(key)).isEqualTo(value);
    }
}
