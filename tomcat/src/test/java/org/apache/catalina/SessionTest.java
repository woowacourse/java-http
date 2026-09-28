package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static support.ConcurrentTestSupport.runConcurrently;

import com.techcourse.model.User;
import org.junit.jupiter.api.Test;

public class SessionTest {

    @Test
    public void session_success_save_and_inquiry() {
        // given
        final Session session = new Session("session-id");
        final User user = new User(
                "gugu",
                "password",
                "gugu@email.com"
        );

        // when
        session.setAttribute("user", user);

        //then
        assertThat(session.getAttribute("user"))
                .isEqualTo(user);
    }

    @Test
    void session_attributes_add_find_and_remove_success_concurrently() throws Exception {
        // given
        final Session session = new Session("concurrent-session-id");
        final int attributeCount = 1000;
        final int workerCount = 8;

        // when
        runConcurrently(workerCount, worker -> {
            for (int i = worker; i < attributeCount; i += workerCount) {
                session.setAttribute("attribute-" + i, i);
                assertThat(session.getAttribute("attribute-" + i)).isEqualTo(i);
            }
        });

        // then
        for (int i = 0; i < attributeCount; i++) {
            assertThat(session.getAttribute("attribute-" + i)).isEqualTo(i);
        }

        // when
        runConcurrently(workerCount, worker -> {
            for (int i = worker; i < attributeCount; i += workerCount) {
                session.removeAttribute("attribute-" + i);
            }
        });

        // then
        for (int i = 0; i < attributeCount; i++) {
            assertThat(session.getAttribute("attribute-" + i)).isNull();
        }
    }

    @Test
    void session_null_attribute_removes_value() {
        // given
        final Session session = new Session("session-id");
        session.setAttribute("user", "gugu");

        // when
        session.setAttribute("user", null);

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void session_invalidate_clears_attributes() {
        // given
        final Session session = new Session("session-id");
        session.setAttribute("user", "gugu");
        session.setAttribute("role", "admin");

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("user")).isNull();
        assertThat(session.getAttribute("role")).isNull();
    }
}
