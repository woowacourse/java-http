package servlet.http.request.uri;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class URITest {

    @Test
    void URI_객체를_생성한다() {
        // given
        String uri = "/users?team=ddangkong";

        // when
        URI actual = new URI(uri);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getPath()).isEqualTo("/users");
            softly.assertThat(actual.getQueryParamValue("team")).isEqualTo("ddangkong");
        });
    }

    @Test
    void URI가_비어있으면_예외가_발생한다() {
        // given
        String uri = null;

        // when & then
        assertThatThrownBy(() -> new URI(uri))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("URI는 필수입니다.");
    }

    @Test
    void URI를_분리했을_때_path가_없으면_예외가_발생한다() {
        // given
        String uri = "?team=ddangkong";

        // when & then
        assertThatThrownBy(() -> new URI(uri))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Path는 필수입니다.");
    }

    @Test
    void URI를_분리했을_때_길이가_2를_초과하면_예외가_발생한다() {
        // given
        String uri = "/users?team=ddangkong?name=prin";

        // when & then
        assertThatThrownBy(() -> new URI(uri))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("잘못된 URI입니다. uri: /users?team=ddangkong?name=prin");
    }

    @Test
    void  URI를_분리했을_때_query_parameter가_없으면_QueryParams는_비어있다() {
        // given
        String uri = "/users";

        // when
        URI actual = new URI(uri);

        // then
        assertThat(actual.existQueryParams()).isFalse();
    }
}
