package nextstep.jwp.httpserver.domain.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModelAndView 도메인 테스트")
class ModelAndViewTest {

    @Test
    @DisplayName("view이름이 resource 파일인 경우")
    void isResourceFile() {
        // given
        ModelAndView mv = new ModelAndView("/index.html");

        // when
        boolean isResource = mv.isResourceFile();

        // then
        assertThat(isResource).isTrue();
    }

    @Test
    @DisplayName("view이름이 resource 파일이 아닌 경우")
    void noResourceFile() {
        // given
        ModelAndView mv = new ModelAndView("/login");

        // when
        boolean isResource = mv.isResourceFile();

        // then
        assertThat(isResource).isFalse();
    }

}
