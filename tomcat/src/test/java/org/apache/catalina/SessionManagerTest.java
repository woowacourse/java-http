package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

class SessionManagerTest {

    Map<String, User> threadSafeSessions = new ConcurrentHashMap<>();
    Map<String, User> nonThreadSafeSessions = new HashMap<>();

    @DisplayName("ConcurrentHashMap을 사용하면 thread-safe 하다.")
    @RepeatedTest(value = 50)
    void threadSafe() throws InterruptedException {
        int repeat = 1000;

        Thread[] threads = new Thread[repeat];

        for (int i = 0; i < repeat; i++) {
            String sessionId = String.valueOf(i);
            threads[i] = new Thread(() -> {
                threadSafeSessions.put(sessionId, new User(null, null, null, null));
                nonThreadSafeSessions.put(sessionId, new User(null, null, null, null));
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("threadSafeSessions: " + threadSafeSessions.size());
        System.out.println("nonThreadSafeSessions: " + nonThreadSafeSessions.size());

        assertThat(threadSafeSessions.size()).isEqualTo(repeat);
//        assertThat(nonThreadSafeSessions.size()).isNotEqualTo(repeat);    // 우연히 결과가 1000이 나올 수 있음
    }
}
