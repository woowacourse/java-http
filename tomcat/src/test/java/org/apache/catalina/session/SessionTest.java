package org.apache.catalina.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionTest {

    @Test
    @DisplayName("동시에 저장한 세션 속성이 유실되지 않는다.")
    void retainAttributesWrittenConcurrently() throws Exception {
        Session session = new Session("concurrent-session");
        CyclicBarrier barrier = new CyclicBarrier(8);
        var executor = Executors.newFixedThreadPool(8);

        try {
            List<Future<?>> tasks = new ArrayList<>();
            for (int worker = 0; worker < 8; worker++) {
                String prefix = worker + ":";
                tasks.add(executor.submit(() -> {
                    barrier.await(5, TimeUnit.SECONDS);
                    for (int i = 0; i < 500; i++) {
                        session.setAttribute(prefix + i, i);
                    }
                    return null;
                }));
            }
            for (Future<?> task : tasks) {
                task.get(5, TimeUnit.SECONDS);
            }

            for (int worker = 0; worker < 8; worker++) {
                for (int i = 0; i < 500; i++) {
                    assertThat(session.getAttribute(worker + ":" + i)).isEqualTo(i);
                }
            }
        } finally {
            executor.shutdownNow();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    @DisplayName("세션 속성의 이름과 값은 null을 허용하지 않는다.")
    void rejectNullAttribute() {
        Session session = new Session("session");

        assertThatThrownBy(() -> session.setAttribute(null, "value"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> session.setAttribute("name", null))
                .isInstanceOf(NullPointerException.class);
    }
}
