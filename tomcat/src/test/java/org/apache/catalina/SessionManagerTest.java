package org.apache.catalina;

import org.junit.jupiter.api.RepeatedTest;

import static java.lang.Math.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SessionManagerTest {

    @RepeatedTest(10000)
    void threadSafe() {
        //given
        final var session = new Session("id");
        session.putValue("hi", 1);
        SessionManager.add(session);

        //when
        int first = (int)(random()*10);
        System.out.println("first = " + first);
        final var thread1 = new Thread(() -> {
            final var session1 = SessionManager.findSession("id");
            int firstGetValue = (int) session.getValue("hi");
            System.out.println("firstGetValue = " + firstGetValue);
            session1.putValue("hi", firstGetValue + first);
        });
        int second = (int)(random()*10);
        System.out.println("second = " + second);
        final var thread2 = new Thread(() -> {
            final var session2 = SessionManager.findSession("id");
            int secondGetValue = (int) session.getValue("hi");
            System.out.println("secondGetValue = " + secondGetValue);
            session2.putValue("hi", secondGetValue - second);
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            fail();
        }

        //then
        assertEquals(1 + first - second, session.getValues().get("hi"));
    }

}
