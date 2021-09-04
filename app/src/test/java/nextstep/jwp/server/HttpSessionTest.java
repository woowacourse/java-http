package nextstep.jwp.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.SessionAttributeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpSessionTest {

    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        httpSession = new HttpSession("1L");
    }

    @DisplayName("Attribute를 가지고 있는지 물어봤을 때")
    @Nested
    class hasAttribute {

        @DisplayName("가지고 있다면 true를 반환한다.")
        @Test
        void hasAttributeTrue() {
            // given
            String name = "attribute-name";
            httpSession.setAttribute(name, "value");

            // when, then
            assertThat(httpSession.hasAttribute(name)).isTrue();
        }

        @DisplayName("가지고 있지 않다면 false를 반환한다.")
        @Test
        void hasAttributeFalse() {
            // given
            String name = "attribute-name";

            // when, then
            assertThat(httpSession.hasAttribute(name)).isFalse();
        }
    }

    @DisplayName("Attribute를 요청했을 때")
    @Nested
    class GetAttribute {

        @DisplayName("해당하는 Attribute가 있다면 반환한다.")
        @Test
        void getAttribute() {
            // given
            String attributeName = "A is";
            String attributeValue = "Apple";

            httpSession.setAttribute("A is", "Apple");

            // when
            String foundAttribute = (String) httpSession.getAttribute(attributeName);

            // then
            assertThat(foundAttribute).isEqualTo(attributeValue);
        }

        @DisplayName("해당하는 Attribute가 없다면 예외가 발생한다.")
        @Test
        void getAttributeException() {
            // when, then
            assertThatThrownBy(() -> httpSession.getAttribute("someThing"))
                .isExactlyInstanceOf(SessionAttributeNotFoundException.class);

        }
    }
}