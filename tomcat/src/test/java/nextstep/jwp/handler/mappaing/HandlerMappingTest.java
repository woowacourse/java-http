package nextstep.jwp.handler.mappaing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.SignUpRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("HandlerMapping 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HandlerMappingTest {

    @Test
    void 요청을_처리할_수_있는_핸들러를_찾는다() {
        // given
        HandlerMapping mapping = new HandlerMapping(Map.of(
                "/", new RootPageRequestHandler(),
                "/login", new LoginHandler(),
                "/register", new SignUpRequestHandler(),
                "/**", new StaticResourceRequestHandler()
        ));
        RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1");
        HttpRequest request = HttpRequest.builder().requestLine(requestLine).build();

        // when
        Controller handler = mapping.getHandler(request);

        // then
        assertThat(handler).isInstanceOf(LoginHandler.class);
    }
}
