package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.common.TestUtil;
import nextstep.jwp.framework.config.FactoryConfiguration;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.webserver.RequestHandler;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.networkHandler());

        // when
        requestHandler.run();

        // then
        assertThat(socket.output())
            .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.OK));
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.networkHandler());

        // when
        requestHandler.run();

        // then
        assertThat(socket.output())
            .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.OK));
    }
}
