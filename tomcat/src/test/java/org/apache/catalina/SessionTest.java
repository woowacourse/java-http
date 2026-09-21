package org.apache.catalina;

import com.techcourse.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SessionTest {

    private static final String SESSION_ID = "656cef62-e3c4-40bc-a8df-94732920ed46";

    @Test
    @DisplayName("생성할 때 받은 아이디를 그대로 돌려준다")
    void keepsGivenId() {
        final Session session = new Session(SESSION_ID);

        assertThat(session.getId()).isEqualTo(SESSION_ID);
    }

    @Nested
    @DisplayName("값을 담고 꺼낸다")
    class Attributes {

        @Test
        @DisplayName("담은 값을 이름으로 꺼낼 수 있다")
        void storesAndReadsAttribute() {
            final Session session = new Session(SESSION_ID);

            session.setAttribute("user", "gugu");

            assertThat(session.getAttribute("user")).isEqualTo("gugu");
        }

        @Test
        @DisplayName("담은 적 없는 이름으로 꺼내면 null이다")
        void returnsNullForUnknownName() {
            final Session session = new Session(SESSION_ID);

            assertThat(session.getAttribute("user")).isNull();
        }

        @Test
        @DisplayName("같은 이름으로 다시 담으면 마지막 값이 남는다")
        void overwritesValueOfSameName() {
            final Session session = new Session(SESSION_ID);

            session.setAttribute("user", "gugu");
            session.setAttribute("user", "lie");

            assertThat(session.getAttribute("user")).isEqualTo("lie");
        }

        @Test
        @DisplayName("여러 이름의 값을 독립적으로 보관한다")
        void keepsAttributesIndependently() {
            final Session session = new Session(SESSION_ID);

            session.setAttribute("user", "gugu");
            session.setAttribute("cart", 3);

            assertThat(session.getAttribute("user")).isEqualTo("gugu");
            assertThat(session.getAttribute("cart")).isEqualTo(3);
        }

        @Test
        @DisplayName("타입에 관계없이 담을 수 있고, 꺼낼 때 원래 타입으로 형변환한다")
        void storesAnyTypeOfValue() {
            final Session session = new Session(SESSION_ID);
            final User user = new User("gugu", "password", "hkkang@woowahan.com");

            session.setAttribute("user", user);

            final User found = (User) session.getAttribute("user");
            assertThat(found).isSameAs(user);
            assertThat(found.getAccount()).isEqualTo("gugu");
        }
    }

    @Nested
    @DisplayName("값을 지운다")
    class Removal {

        @Test
        @DisplayName("지운 이름으로 꺼내면 null이다")
        void removesAttribute() {
            final Session session = new Session(SESSION_ID);
            session.setAttribute("user", "gugu");

            session.removeAttribute("user");

            assertThat(session.getAttribute("user")).isNull();
        }

        @Test
        @DisplayName("담은 적 없는 이름을 지워도 예외가 나지 않는다")
        void doesNotThrowWhenRemovingUnknownName() {
            final Session session = new Session(SESSION_ID);

            assertThatCode(() -> session.removeAttribute("user")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("한 이름을 지워도 다른 값은 남는다")
        void removesOnlyGivenName() {
            final Session session = new Session(SESSION_ID);
            session.setAttribute("user", "gugu");
            session.setAttribute("cart", 3);

            session.removeAttribute("user");

            assertThat(session.getAttribute("user")).isNull();
            assertThat(session.getAttribute("cart")).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("세션을 무효화한다")
    class Invalidate {

        @Test
        @DisplayName("담아둔 값이 모두 사라진다")
        void clearsEveryAttribute() {
            final Session session = new Session(SESSION_ID);
            session.setAttribute("user", "gugu");
            session.setAttribute("cart", 3);

            session.invalidate();

            assertThat(session.getAttribute("user")).isNull();
            assertThat(session.getAttribute("cart")).isNull();
        }

        @Test
        @DisplayName("아이디는 그대로 남는다")
        void keepsId() {
            final Session session = new Session(SESSION_ID);
            session.setAttribute("user", "gugu");

            session.invalidate();

            assertThat(session.getId()).isEqualTo(SESSION_ID);
        }

        @Test
        @DisplayName("무효화한 뒤에도 다시 값을 담을 수 있다")
        void canStoreAgainAfterInvalidate() {
            final Session session = new Session(SESSION_ID);
            session.setAttribute("user", "gugu");
            session.invalidate();

            session.setAttribute("user", "lie");

            assertThat(session.getAttribute("user")).isEqualTo("lie");
        }
    }
}
