package nextstep.joanne.server.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpSessionTest {

    public static final String JSESSIONID = "JSESSIONID";

    @DisplayName("Attribute 삽입한다.")
    @Test
    void setAttribute() {
        // given
        HttpSession httpSession = HttpSession.of(JSESSIONID);

        // when
        assertThatCode(() -> httpSession.setAttribute("joanne", "1234"))
                .doesNotThrowAnyException();
    }

    @DisplayName("Attribute에 해당하는 obj를 가져온다.")
    @Test
    void getAttribute() {
        // given
        HttpSession httpSession = HttpSession.of(JSESSIONID);

        // when
        httpSession.setAttribute("joanne", "1234");

        // then
        assertThat(httpSession.getAttribute("joanne")).isEqualTo("1234");
    }

    @DisplayName("Attribute를 지운다.")
    @Test
    void removeAttribute() {
        // given
        HttpSession httpSession = HttpSession.of(JSESSIONID);

        // when
        httpSession.setAttribute("joanne", "1234");
        assertThat(httpSession.getAttribute("joanne")).isEqualTo("1234");

        // then
        httpSession.removeAttribute("joanne");
        assertThat(httpSession.getAttribute("joanne")).isNull();
    }

    @DisplayName("HttpSession을 만료시킨다.")
    @Test
    void expire() {
        /// given
        HttpSession httpSession = HttpSession.of(JSESSIONID);

        // when
        httpSession.setAttribute("joanne", "1234");

        // then
        httpSession.expire();
        assertThat(httpSession.getValues()).isEmpty();
    }
}
