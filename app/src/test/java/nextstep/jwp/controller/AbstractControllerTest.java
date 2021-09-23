package nextstep.jwp.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.HashMap;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.ResponseHeaders;
import nextstep.jwp.http.request.HttpHeaders;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private final Controller controller = new AbstractController() {
        @Override
        protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
            super.doPost(request, response);
        }

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
            super.doGet(request, response);
        }
    };

    @DisplayName("잘못된 Http Method 로 접근")
    @Test
    void notAllowedMethod() {
        final HttpRequest httpRequest = new HttpRequest(
                HttpMethod.GET,
                "/index.html",
                "HTTP/1.1",
                new HttpHeaders(new HashMap<>(), new HttpCookie())
        );
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        assertThatThrownBy(() -> controller.service(httpRequest, httpResponse))
                .isInstanceOf(MethodNotAllowedException.class);
    }
}
