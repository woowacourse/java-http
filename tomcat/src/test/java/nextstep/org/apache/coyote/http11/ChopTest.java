package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class ChopTest {

    @Test
    void test() {
        String chop = StringUtils.chop("/");
        assertThat(chop).isEqualTo("");
    }

    @Test
    void test2() {
        String chop = StringUtils.chop("/test.html/");
        assertThat(chop).isEqualTo("/test.html");
    }

    @Test
    void test3() {
        String chop = StringUtils.chop("");
        assertThat(chop).isEqualTo("");
    }
}
