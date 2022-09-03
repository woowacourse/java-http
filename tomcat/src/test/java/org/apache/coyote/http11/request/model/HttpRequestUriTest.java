package org.apache.coyote.http11.request.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.UnSupportedMediaType;
import org.apache.coyote.http11.model.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestUriTest {

    @Test
    @DisplayName("html파일의 contentType을 가져온다.")
    void getHtmlContentType() {
        HttpRequestUri httpRequestUri = new HttpRequestUri("index.html");

        ContentType contentType = httpRequestUri.getContentType();

        assertThat(contentType).isEqualTo(ContentType.TEXT_HTML_CHARSET_UTF_8);
    }

    @Test
    @DisplayName("html파일의 contentType을 가져온다.")
    void getCssContentType() {
        HttpRequestUri httpRequestUri = new HttpRequestUri("styles.css");

        ContentType contentType = httpRequestUri.getContentType();

        assertThat(contentType).isEqualTo(ContentType.TEXT_CSS_CHARSET_UTF_8);
    }

    @Test
    @DisplayName("html파일의 contentType을 가져온다.")
    void getJsContentType() {
        HttpRequestUri httpRequestUri = new HttpRequestUri("scripts.js");

        ContentType contentType = httpRequestUri.getContentType();

        assertThat(contentType).isEqualTo(ContentType.TEXT_JS_CHARSET_UTF_8);
    }

    @Test
    @DisplayName("존재하지 않는 형식의 ContentType을 요청할 경우 예외를 발생한다.")
    void unsupportedMediaType() {
        HttpRequestUri httpRequestUri = new HttpRequestUri("noooo");

        assertThatThrownBy(httpRequestUri::getContentType)
                .hasMessage("not found type : noooo")
                .isInstanceOf(UnSupportedMediaType.class);
    }

    @Test
    @DisplayName("해당요청이 인덱스 요청인지 확인한다.")
    void isIndex() {
        HttpRequestUri httpRequestUri = new HttpRequestUri("/");

        assertThat(httpRequestUri.isIndex()).isTrue();
    }

    @Test
    @DisplayName("해당요청이 쿼리 스트링 요청인지 확인한다.")
    void isQueryString() {
        HttpRequestUri httpRequestUri = new HttpRequestUri("/login?account=gugu&password=password");

        assertThat(httpRequestUri.isQuery()).isTrue();
    }
}
