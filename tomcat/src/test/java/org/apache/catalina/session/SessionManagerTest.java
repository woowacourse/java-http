package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("동시성 컬렉션을 사용하면 스레드 안정성과 원자성을 보장할 수 있다.")
    @Test
    void threadSafetyUsingConcurrentCollections() {
        Map<Integer, Integer> hashMap = new HashMap<>();
        Map<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();

        int nThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < nThreads; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 10000; j++) {
                    hashMap.put(j, j);
                    concurrentHashMap.put(j, j);
                }
            });
        }

        executorService.shutdown();

        assertAll(
                () -> assertThat(hashMap.size()).isNotEqualTo(10000),
                () -> assertThat(concurrentHashMap).hasSize(10000)
        );
    }
}
