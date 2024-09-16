package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Nested
    class 세션 {

        @BeforeEach
        void setUp() {
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.clear();
        }

        @Test
        void 세션이_성공적으로_등록되고_확인된다() {
            // given
            SessionManager sessionManager = SessionManager.getInstance();
            String id = "ABCD";
            Session session = new Session(id);
            sessionManager.add(session);

            // when
            Session actual = sessionManager.findSession(id);

            // then
            assertThat(actual).isEqualTo(session);
        }

        @Test
        void 존재하지_않는_세션을_확인할_수_없다() {
            // given
            SessionManager sessionManager = SessionManager.getInstance();
            String id = "ABCD";
            Session session = new Session(id);
            sessionManager.add(session);

            // when
            Session actual = sessionManager.findSession("NotExist");

            // then
            assertThat(actual).isNull();
        }

        @Test
        void 동시_요청을_처리할_수_있다() throws InterruptedException{
            // given
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            SessionManager sessionManager = SessionManager.getInstance();

            // when
            for (int i = 0; i < 10000; i++) {
                int finalI = i;
                executorService.submit(() -> sessionManager.add(new Session(finalI + "")));
            }
            executorService.awaitTermination(10000, TimeUnit.MICROSECONDS);

            // then
            for (int i = 0; i < 10000; i++) {
                assertThat(sessionManager.findSession(i + "")).isNotNull();
            }
        }
    }
}
