package nextstep.jwp.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.SessionAttributeNotFoundException;
import nextstep.jwp.exception.SessionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {

    private static final String SESSION_ID = "1";
    private static final String ATTRIBUTE_NAME = "A is";
    private static final String ATTRIBUTE_VALUE = "Apple";

    private HttpSessions httpSessions;

    @BeforeEach
    void setUp() {
        HttpSession httpSession = new HttpSession(SESSION_ID);
        httpSession.setAttribute(ATTRIBUTE_NAME, ATTRIBUTE_VALUE);

        httpSessions = new HttpSessions();
        httpSessions.addSession(httpSession);
    }

    @DisplayName("sessionId와 attributeName을 통해 Object가 있는지 질문시")
    @Nested
    class hasObject {
        @DisplayName("해당하는 session이 있을 때")
        @Nested
        class hasSession {
            @DisplayName("해당하는 attribute가 있다면 true를 반환한다.")
            @Test
            void returnTrue() {
                assertThat(httpSessions.hasObject(SESSION_ID, ATTRIBUTE_NAME)).isTrue();
            }

            @DisplayName("해당하는 attribute가 없다면 false를 반환한다.")
            @Test
            void returnFalse() {
                assertThat(httpSessions.hasObject(SESSION_ID, "noAttribute")).isFalse();
            }
        }

        @DisplayName("해당되는 session이 없을 때")
        @Nested
        class haveNotSession {

            @DisplayName("false를 반환한다.")
            @Test
            void returnFalse() {
                assertThat(httpSessions.hasObject("noSession", "noName")).isFalse();
            }
        }
    }

    @DisplayName("sessionId와 attributeName을 통해 Object 조회 요청시")
    @Nested
    class FindObject {

        @DisplayName("sessionId에 해당하는 Session을 찾았을 때")
        @Nested
        class containsSession {

            @DisplayName("attributeName에 해당하는 attribute를 찾으면 Object를 반환한다.")
            @Test
            void getAttribute() {
                // when
                String attribute = (String) httpSessions.findObject(SESSION_ID, ATTRIBUTE_NAME);

                // then
                assertThat(attribute).isEqualTo(ATTRIBUTE_VALUE);
            }

            @DisplayName("attributeName에 해당하는 attribute가 없으면 예외가 발생한다.")
            @Test
            void getAttributeException() {
                // when, then
                assertThatThrownBy(() -> httpSessions.findObject(SESSION_ID, "Something"))
                    .isExactlyInstanceOf(SessionAttributeNotFoundException.class);
            }
        }

        @DisplayName("sessionId에 해당하는 Session을 찾지 못했을 때 예외가 발생한다.")
        @Test
        void findObjectException() {
            // when, then
            assertThatThrownBy(() -> httpSessions.findObject("Another id", ATTRIBUTE_NAME))
                .isExactlyInstanceOf(SessionNotFoundException.class);
        }
    }
}