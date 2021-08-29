package nextstep.jwp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WebServerTest {

    @Test
    @DisplayName("webServer test")
    public void webServer() throws Exception{
        final WebServer webServer = new WebServer(8888);
        Assertions.assertThat(webServer).isNotNull();
    }
}
