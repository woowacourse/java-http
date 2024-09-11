package servlet.http.request.uri;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    void Path_객체를_생성한다() {
        // given
        String path = "/users";

        // when
        Path actual = new Path(path);

        // then
        assertThat(actual.getPath()).isEqualTo(path);
    }

    @Test
    void Path가_비어있으면_예외가_발생한다() {
        // given
        String path = null;

        // when & then
        assertThatThrownBy(() -> new Path(path))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path는 필수입니다.");
    }

    @Test
    void Path가_슬래시로_시작하지_않으면_예외가_발생한다() {
        // given
        String path = "users";

        // when & then
        assertThatThrownBy(() -> new Path(path))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path는 /로 시작해야 합니다.");
    }
}
