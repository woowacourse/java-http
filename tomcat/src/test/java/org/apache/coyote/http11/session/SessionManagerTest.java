package org.apache.coyote.http11.session;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.RepeatedTest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @RepeatedTest(3)
    void 여러_쓰레드가_읽고_쓰기를_해도_동시성_문제가_없다() {
        Session session = new Session();
        SessionManager.add(session);

        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10_000_000; i++) {
                SessionManager.add(new Session());
            }
        });
        thread.start();

        for (int i = 0; i < 10_000_000; i++) {
            if (SessionManager.findSession(session.getId()) == null) {
                fail("동시성 문제 발생");
            }
        }
        thread.interrupt();
    }
}
