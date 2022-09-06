package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MapTest {

    @DisplayName("isEmpty")
    @Test
    void isEmpty() {
        HashMap<String, String> map = new HashMap<>();

        assertThat(map.isEmpty()).isTrue();
    }
}
