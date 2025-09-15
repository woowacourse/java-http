package common;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    @DisplayName("파일 경로에서 Content-Type 결정 - 확장자 매핑")
    void fromPathFilePath_extensionMapping() {
        // given
        final String html = "/index.html";
        final String css = "/assets/style.CSS";
        final String js = "/js/app.Js";

        // when
        final ContentType fromHtml = ContentType.fromFilePath(html);
        final ContentType fromCss = ContentType.fromFilePath(css);
        final ContentType fromJs = ContentType.fromFilePath(js);

        // then
        assertSoftly(softly -> {
            softly.assertThat(fromHtml).isEqualTo(ContentType.DEFAULT_RESPONSE_CONTENT_TYPE);
            softly.assertThat(fromCss).isEqualTo(ContentType.TEXT_CSS);
            softly.assertThat(fromJs).isEqualTo(ContentType.APPLICATION_JAVASCRIPT);
        });
    }

    @Test
    @DisplayName("파일 경로에서 Content-Type 결정 - 미매핑 확장자는 기본값")
    void fromPathFilePath_defaultOnUnknown() {
        // given
        final String png = "/images/logo.png";
        final String noExt = "/no-extension";

        // when
        final ContentType fromPng = ContentType.fromFilePath(png);
        final ContentType fromNoExt = ContentType.fromFilePath(noExt);

        // then
        assertSoftly(softly -> {
            softly.assertThat(fromPng).isEqualTo(ContentType.DEFAULT_RESPONSE_CONTENT_TYPE);
            softly.assertThat(fromNoExt).isEqualTo(ContentType.DEFAULT_RESPONSE_CONTENT_TYPE);
        });
    }

    @Test
    @DisplayName("헤더에서 Content-Type 결정 - 기본값 및 파라미터 무시")
    void fromFilePathHeader_withParamsAndDefaults() {
        // given
        final String nullHeader = null;
        final String emptyHeader = "";
        final String html = "text/html";
        final String htmlWithParam = "text/html; charset=UTF-8";
        final String form = "application/x-www-form-urlencoded";
        final String unknown = "application/unknown";

        // when & then
        assertSoftly(softly -> {
            softly.assertThat(ContentType.fromHeader(nullHeader)).isEqualTo(ContentType.TEXT_PLAIN);
            softly.assertThat(ContentType.fromHeader(emptyHeader)).isEqualTo(ContentType.TEXT_PLAIN);
            softly.assertThat(ContentType.fromHeader(html)).isEqualTo(ContentType.TEXT_HTML);
            softly.assertThat(ContentType.fromHeader(htmlWithParam)).isEqualTo(ContentType.TEXT_HTML);
            softly.assertThat(ContentType.fromHeader(form)).isEqualTo(ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
            softly.assertThat(ContentType.fromHeader(unknown)).isEqualTo(ContentType.TEXT_PLAIN);
        });
    }

    @Test
    @DisplayName("MIME과 charset 조합 반환")
    void getMimeTypeAndCharset() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThat(ContentType.TEXT_HTML.getMimeTypeAndCharset())
                    .isEqualTo("text/html;charset=UTF-8");
            softly.assertThat(ContentType.TEXT_CSS.getMimeTypeAndCharset())
                    .isEqualTo("text/css;charset=UTF-8");
            softly.assertThat(ContentType.APPLICATION_JAVASCRIPT.getMimeTypeAndCharset())
                    .isEqualTo("application/javascript;charset=UTF-8");
        });
    }
}
