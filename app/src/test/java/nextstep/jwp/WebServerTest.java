package nextstep.jwp;

import nextstep.jwp.mvc.exception.PageNotFoundException;
import nextstep.jwp.webserver.response.StatusCode;
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

    @Test
    @DisplayName("PageNotFoundException test")
    public void pageNotFound() throws Exception{
        //given
        final PageNotFoundException pageNotFoundException = new PageNotFoundException();
        Assertions.assertThat(pageNotFoundException.getPage()).isEqualTo("404.html");
        Assertions.assertThat(pageNotFoundException.getStatusCode()).isEqualTo(StatusCode.NOT_FOUND);
        //when

        //then
    }
}
