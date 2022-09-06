package org.apache.coyote.http11.request.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.UnSupportedMediaType;
import org.apache.coyote.http11.model.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpPathTest {

    @Test
    @DisplayName("html파일의 contentType을 가져온다.")
    void getHtmlContentType() {
        HttpPath httpPath = new HttpPath("index.html");

        ContentType contentType = httpPath.getContentType();

        assertThat(contentType).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    @Test
    @DisplayName("html파일의 contentType을 가져온다.")
    void getCssContentType() {
        HttpPath httpPath = new HttpPath("styles.css");

        ContentType contentType = httpPath.getContentType();

        assertThat(contentType).isEqualTo(ContentType.TEXT_CSS_CHARSET_UTF_8);
    }

    @Test
    @DisplayName("html파일의 contentType을 가져온다.")
    void getJsContentType() {
        HttpPath httpPath = new HttpPath("scripts.js");

        ContentType contentType = httpPath.getContentType();

        assertThat(contentType).isEqualTo(ContentType.TEXT_JS_CHARSET_UTF_8);
    }

    @Test
    @DisplayName("존재하지 않는 형식의 ContentType을 요청할 경우 예외를 발생한다.")
    void unsupportedMediaType() {
        HttpPath httpPath = new HttpPath("noooo");

        assertThatThrownBy(httpPath::getContentType)
                .hasMessage("not found type : noooo")
                .isInstanceOf(UnSupportedMediaType.class);
    }

    @Test
    @DisplayName("해당요청이 쿼리 스트링 요청인지 확인한다.")
    void isQueryString() {
        HttpPath httpPath = new HttpPath("/login?account=gugu&password=password");

        assertThat(httpPath.isQuery()).isTrue();
    }
}
