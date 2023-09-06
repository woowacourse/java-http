package nextstep.org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

public class StudyTest {

    @Test
    void study() {
        //given
        final String s = "a";
        final String[] split = s.split("\\?");

        //when
        final String s1 = split[0];

        //then
        s1.equals("a");
    }
}
