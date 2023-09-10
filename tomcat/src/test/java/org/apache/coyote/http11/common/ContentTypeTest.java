package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ContentTypeTest {

    @Nested
    class ExtractContentTypeTest {

        @ParameterizedTest(name = ".html 확장자 명을 통해, ContentType을 추출할 수 있다.")
        @ValueSource(strings = {"test.html", "/home/main.html", "/login.html"})
        void extractTextHtmlSuccessTest(String nativePath) {
            assertThat(ContentType.extractValueFromPath(nativePath)).isEqualTo(ContentType.TEXT_HTML.getValue());
        }

        @ParameterizedTest(name = ".css 확장자 명을 통해, ContentType을 추출할 수 있다.")
        @ValueSource(strings = {"test.css", "/home/main.css", "/login.css"})
        void extractTextCssSuccessTest(String nativePath) {
            assertThat(ContentType.extractValueFromPath(nativePath)).isEqualTo(ContentType.TEXT_CSS.getValue());
        }

        @ParameterizedTest(name = ".js 확장자 명을 통해, ContentType을 추출할 수 있다.")
        @ValueSource(strings = {"test.js", "/home/main.js", "/login.js"})
        void extractJSSuccessTest(String nativePath) {
            assertThat(ContentType.extractValueFromPath(nativePath))
                    .isEqualTo(ContentType.APPLICATION_JAVASCRIPT.getValue());
        }
        
    }

}