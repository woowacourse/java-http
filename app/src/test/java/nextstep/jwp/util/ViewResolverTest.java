package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @Test
    void staticValue() throws IOException {
        //given
        String path = "/nextstep.txt";
        ViewResolver viewResolver = new ViewResolver(path);
        String answer = "nextstep";

        //when
        String result = viewResolver.staticValue("txt");

        //then
        assertThat(result).isEqualTo(answer);
    }
}