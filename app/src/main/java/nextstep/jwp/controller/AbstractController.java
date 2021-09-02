package nextstep.jwp.controller;

import nextstep.jwp.http.*;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static nextstep.jwp.controller.JwpController.NOT_FOUND_RESPONSE;

public abstract class AbstractController implements Controller {
    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected HttpResponse getHttpResponse(HttpRequest request, Map<String, Function<HttpRequest, HttpResponse>> mappedFunction) {
        return mappedFunction.entrySet().stream()
                .filter(entry -> request.containsFunctionInUrl(entry.getKey()))
                .map(entry -> entry.getValue().apply(request))
                .findAny()
                .orElse(NOT_FOUND_RESPONSE);
    }

    protected HttpCookie addCookie(HttpRequest request, User user) {
        HttpCookie httpCookie = new HttpCookie(request.getRequestHeaders());
        if (!httpCookie.containsJSession()) {
            String id = makeRandomId();
            HttpSession httpSession = new HttpSession(id);
            httpSession.setAttribute("user", user);
            HttpSessions.addSession(httpSession);

            httpCookie.setCookies(id);
            return httpCookie;
        }
        String existJSessionCookie = httpCookie.getJSessionCookie();
        HttpSessions.remove(existJSessionCookie);
        return addCookie(request, user);
    }

    protected HttpCookie findJSessionCookie(HttpRequest request) {
        HttpCookie httpCookie = new HttpCookie(request.getRequestHeaders());
        String jSessionCookieId = httpCookie.getJSessionCookie();
        Object user = HttpSessions.getSession(jSessionCookieId).getAttribute("user");
        log.info("로그인 유저: {}", user);
        return httpCookie;
    }

    private String makeRandomId() {
        return UUID.randomUUID().toString();
    }
}
