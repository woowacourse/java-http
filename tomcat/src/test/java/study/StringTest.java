package study;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringTest {

    @DisplayName("contains")
    @Test
    void contains() {
        String string = "index.html";

        assertThat(string.contains(".")).isTrue();
        assertThat(string.contains("a")).isFalse();
    }
}
