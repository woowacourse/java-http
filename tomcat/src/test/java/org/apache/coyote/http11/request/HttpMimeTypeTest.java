package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class HttpMimeTypeTest {

    @Test
    @DisplayName("확장자로 해당하는 mime type을 찾을 수 있다.")
    void from() {
        String html = "html";
        String css = "css";
        String svg = "svg";

        assertAll(() -> {
                    assertThat(HttpMimeType.from(html)).isEqualTo(HttpMimeType.TEXT_HTML);
                    assertThat(HttpMimeType.from(css)).isEqualTo(HttpMimeType.TEXT_CSS);
                    assertThat(HttpMimeType.from(svg)).isEqualTo(HttpMimeType.IMG_SVG);
                });
    }

    @Test
    @DisplayName("확장자가 잘못된 경우 octet-stream을 반환한다.")
    void from_invalid() {
        String invalid = "feopqfpqenopqn";

        assertThat(HttpMimeType.from(invalid)).isEqualTo(HttpMimeType.OCTET_STREAM);
    }

    @Test
    @DisplayName("mimeType을 문자열로 변환할 수 있다.")
    void asString() {
        String actual = HttpMimeType.TEXT_HTML.asString();

        assertThat(actual).contains("text/html");
    }

    @Test
    @DisplayName("text 형식인 경우 charset을 함께 반환한다.")
    void asString_charset() {
        String actual = HttpMimeType.TEXT_HTML.asString();

        assertThat(actual).contains("charset=");
    }
}
