package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.common.TestUtil;
import nextstep.jwp.framework.config.FactoryConfiguration;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.webserver.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.requestMapping());

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
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.requestMapping());

        // when
        requestHandler.run();

        // then
        assertThat(socket.output())
            .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.OK));
    }

    @DisplayName("허용되지 않은 경로로 접근시 404 페이지가 반환된다.")
    @Test
    void it_returns_404_when_invalid_path() {
        // given
        final String httpRequest= String.join("\r\n",
            "GET /alalala HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.requestMapping());

        // when
        requestHandler.run();

        // then
        assertThat(socket.output())
            .isEqualTo(TestUtil.writeResponse("/404.html", HttpStatus.NOT_FOUND));
    }

    @DisplayName("잘못된 형식의 메시지 요청시 500 페이지로 이동된다.")
    @Test
    void it_returns_500_when_invalid_message_format() {
        // given
        final String httpRequest= String.join("\r\n",
            "GET /alalala ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.requestMapping());

        // when
        requestHandler.run();

        // then
        assertThat(socket.output())
            .isEqualTo(TestUtil.writeResponse("/500.html", HttpStatus.INTERNAL_SEVER_ERROR));
    }

    @DisplayName("Content Body를 넣어 POST 요청시 정상적으로 요청을 처리한다.")
    @Test
    void it_returns_302_when_deal_with_post_request() {
        // given
        final String httpRequest= String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 30",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket, FactoryConfiguration.requestMapping());

        // when
        requestHandler.run();

        // then
        assertThat(socket.output())
            .isEqualTo(TestUtil.writeResponse("/index.html", HttpStatus.FOUND));
    }
}
