package nextstep.jwp.web.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Content-Type 관련 로직을 테스트한다.")
class ContentTypeTest {

    @DisplayName("확장자에 따른 Content-Type enum 값을 반환한다. - .html")
    @Test
    void findContentType_html() {
        // given
        ContentType expected = ContentType.HTML;

        // when
        ContentType actual = ContentType.findContentType(".html");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("확장자에 따른 Content-Type enum 값을 반환한다. - .js")
    @Test
    void findContentType_js() {
        // given
        ContentType expected = ContentType.JS;

        // when
        ContentType actual = ContentType.findContentType(".js");

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("확장자에 따른 Content-Type enum 값을 반환한다. - .css")
    @Test
    void findContentType() {
        // given
        ContentType expected = ContentType.CSS;

        // when
        ContentType actual = ContentType.findContentType(".css");

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
