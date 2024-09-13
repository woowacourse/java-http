package integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;
import support.TestServer;

import static org.assertj.core.api.Assertions.assertThat;

class IndexIntegrationTest {
    @Test
    @DisplayName("/로 접속하면 Hello world! 를 반환한다.")
    void login_success() {
        TestServer.serverStart();

        final var result = TestHttpUtils.sendGet(TestServer.getHost(), "/");

        assertThat(result.body()).isEqualTo("Hello world!\n");
        assertThat(result.statusCode()).isEqualTo(200);
    }
}
