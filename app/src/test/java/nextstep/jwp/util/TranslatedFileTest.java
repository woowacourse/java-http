package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class TranslatedFileTest {

    @Test
    void staticValue() throws IOException {
        //given
        String path = "/nextstep.txt";
        TranslatedFile translatedFile = new TranslatedFile(path);
        String answer = "nextstep";

        //when
        String result = translatedFile.staticValue();

        //then
        assertThat(result).isEqualTo(answer);
    }
}