package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    @Test
    @DisplayName("요청 uri에 해당하는 파일이름을 찾을 수 있다.")
    void process() {
        LoginController controller = new LoginController();

        String result = controller.process("/login?account=gugu&password=password");

        assertThat(result).isEqualTo("/login.html");
    }
}
