package nextstep.jwp.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilsTest {

    @Test
    void readFileOfUrl() throws IOException {
        // given
        String indexBody = FileUtils.readFileOfUrl("/index.html");
        String loginBody = FileUtils.readFileOfUrl("/login.html");
        String registerBody = FileUtils.readFileOfUrl("/register.html");
        String unAuthorizedBody = FileUtils.readFileOfUrl("/401.html");
        String notFoundBody = FileUtils.readFileOfUrl("/404.html");
        String serverErrorBody = FileUtils.readFileOfUrl("/500.html");

        // then
        assertThat(indexBody).isNotEmpty();
        assertThat(loginBody).isNotEmpty();
        assertThat(registerBody).isNotEmpty();
        assertThat(unAuthorizedBody).isNotEmpty();
        assertThat(notFoundBody).isNotEmpty();
        assertThat(serverErrorBody).isNotEmpty();
    }
}