package org.apache.coyote.http11.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpRequestTest {

    @DisplayName("file 호출이 아님")
    @Test
    void isCallApi() {
        // given
        HttpRequest httpRequest = new HttpRequest("/login");

        // when & then
        assertThat(httpRequest.isFindFile()).isFalse();
    }

    @DisplayName("file 호출")
    @Test
    void isNotCallApi() {
        // given
        HttpRequest httpRequest = new HttpRequest("/login.html");

        // when & then
        assertThat(httpRequest.isFindFile()).isTrue();
    }

    @DisplayName("올바른 ContentType 찾기")
    @ParameterizedTest(name = "value = {0}, expected = {1}")
    @CsvSource(value = {"/styles.css, CSS", "/index.html, HTML", "/js/scripts.js, JS", "/favicon.ico, ICO"})
    void findContentType(String path, ContentType expected) {
        // given
        HttpRequest httpRequest = new HttpRequest(path);

        // when & then
        assertThat(httpRequest.findContentType()).isEqualTo(expected);
    }

    @DisplayName("파일 경로 찾기")
    @Test
    void findFilePath() {
        // given
        HttpRequest httpRequest = new HttpRequest("/index.html?key=value");

        // when & then
        assertThat(httpRequest.findFilePath()).isEqualTo(FilePath.INDEX_PAGE);
    }
}
