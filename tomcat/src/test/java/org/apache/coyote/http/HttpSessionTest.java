package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpSessionTest {

    @Test
    void 생성자는_id를_전달하면_HttpSession을_초기화한다() {
        final HttpSession actual = new HttpSession("abcde");

        assertThat(actual).isNotNull();
    }

    @Test
    void setAttribute_메서드는_key와_value를_전달하면_session에_저장한다() {
        final HttpSession session = new HttpSession("abcde");

        assertDoesNotThrow(() -> session.setAttribute("name", "value"));
    }

    @Test
    void getAttribute_메서드는_key를_전달하면_value를_반환한다() {
        final HttpSession session = new HttpSession("abcde");
        session.setAttribute("name", "value");

        final String actual = (String) session.getAttribute("name");

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual).isEqualTo("value");
        });
    }

    @Test
    void removeAttribute_메서드는_key를_전달하면_해당_key에_해당하는_값을_제거한다() {
        final HttpSession session = new HttpSession("abcde");
        session.setAttribute("name", "value");

        SoftAssertions.assertSoftly(softAssertions -> {
            assertDoesNotThrow(() -> session.removeAttribute("name"));
            softAssertions.assertThat(session.getAttribute("name")).isNull();
        });
    }

    @Test
    void invalidate_메서드는_호출하면_session을_무효화한다() {
        final HttpSession session = new HttpSession("abcde");
        session.setAttribute("name", "value");

        SoftAssertions.assertSoftly(softAssertions -> {
            assertDoesNotThrow(session::invalidate);
            softAssertions.assertThat(session.getAttribute("name")).isNull();
        });
    }
}
