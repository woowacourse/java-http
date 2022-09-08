package study;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

public class TempTest {
    @Test
    void a() {
        // given
        AtomicLong a = new AtomicLong(1L);
        // when
        long b = a.getAndIncrement();
        // then
        System.out.println("a = " + a);
    }
}
