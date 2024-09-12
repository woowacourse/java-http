package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class StaticFileResponseUtilsTest {

    @Test
    void isExistFileTest_whenFileExist_returnTrue() {
        String filePath = "/test.html";

        boolean actual = StaticFileResponseUtils.isExistFile(filePath);

        assertThat(actual).isTrue();
    }

    @Test
    void isExistFileTest_whenFileNotExist_returnFalse() {
        String filePath = "/not-exist.html";

        boolean actual = StaticFileResponseUtils.isExistFile(filePath);

        assertThat(actual).isFalse();
    }
}
