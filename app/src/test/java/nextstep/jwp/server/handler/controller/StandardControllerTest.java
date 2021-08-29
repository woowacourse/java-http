package nextstep.jwp.server.handler.controller;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.element.cookie.Cookie;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StandardControllerTest {

    @Test
    void doService() {
        StandardController standardController = new StandardController(new SimpleController());
        Response response = standardController.doService(Fixture.getHttpRequest("index.html"));

        assertThat(response.asString()).contains(
                "Set-Cookie",
                "test=test"
        );
    }

    private static class SimpleController implements Controller {

        @Override
        public Response doService(HttpRequest httpRequest) {
            Cookie cookie = httpRequest.getCookie();
            cookie.put("test", "test");

            return HttpResponse.status(HttpStatus.OK, "index.html");
        }

        @Override
        public boolean isSatisfiedBy(HttpRequest httpRequest) {
            return true;
        }
    }
}
