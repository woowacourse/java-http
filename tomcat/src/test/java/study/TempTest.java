package study;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Test;

public class TempTest {
    @Test
    void aa() throws IOException {
        // given
        String text = "aaa\nbbb\n\nccc";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes())));
        // when

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line + "xx");
        }
        // then
    }
}
