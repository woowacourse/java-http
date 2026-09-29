package org.apache.catalina;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void 속성에_null을_저장하면_기존_값을_삭제한다() {
        // given
        Session session = new Session("session-id");
        session.setAttribute("user", "usher");

        // when
        session.setAttribute("user", null);

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    @Timeout(10)
    void 여러_스레드가_한_세션에_저장한_속성을_모두_조회할_수_있다() throws Exception {
        // given
        Session session = new Session("session-id");
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService workers = Executors.newFixedThreadPool(4);

        try {
            List<Future<Integer>> results = IntStream.range(0, 32)
                    .mapToObj(index -> workers.submit(() -> {
                        start.await();
                        session.setAttribute("key-" + index, index);
                        return index;
                    }))
                    .toList();

            // when
            start.countDown();

            // then
            for (Future<Integer> result : results) {
                int index = result.get(5, TimeUnit.SECONDS);
                assertThat(session.getAttribute("key-" + index)).isEqualTo(index);
            }
        } finally {
            start.countDown();
            workers.shutdownNow();
        }
    }
}
