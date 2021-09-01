package nextstep.project.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.HttpCookie;
import nextstep.jwp.http.message.HttpStatus;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import nextstep.project.db.InMemoryUserRepository;
import nextstep.project.model.User;

public class LoginController extends HttpHandler {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doGet(httpRequest, httpResponse);

        try {
            HttpSession session = httpRequest.getSession()
                .orElseThrow();

            // 세션을 가지고 유저를 가져올 수 있음.
//            User user = (User) session.getAttribute("user");

            httpResponse.addCookie(new HttpCookie("JSESSIONID", session.getId()));

            redirectTo("./index.html", httpResponse);
        } catch (NoSuchElementException e) {
            renderPage(
                "./static/login.html",
                HttpStatus.OK,
                httpResponse
            );
        }
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doPost(httpRequest, httpResponse);
        try {
            Map<String, String> body = parseFormData(httpRequest.getBody());

            User findUser = InMemoryUserRepository.findByAccount(body.get("account"))
                .orElseThrow(UnauthorizedException::new);

            if (!findUser.checkPassword(body.get("password"))) {
                throw new UnauthorizedException();
            }

            HttpSession session = httpRequest.getSession()
                .orElse(new HttpSession(UUID.randomUUID().toString()));
            session.setAttribute("user", findUser);
            HttpSessions.addSession(session);

            httpResponse.addCookie(new HttpCookie("JSESSIONID", session.getId()));

            redirectTo("./index.html", httpResponse);
        } catch (NoSuchElementException e) {
            renderPage(
                "./static/login.html",
                HttpStatus.OK,
                httpResponse
            );
        } catch (UnauthorizedException e) {
            renderPage(
                "./static/401.html",
                HttpStatus.UNAUTHORIZED,
                httpResponse
            );
        }
    }

    private Map<String, String> parseFormData(String requestBody) {
        Map<String, String> requestBodyAsMap = new HashMap<>();
        String[] splittedBody = requestBody.split("&");
        for (String q : splittedBody) {
            String[] keyValue = q.split("=");
            if (2 == keyValue.length) {
                requestBodyAsMap.put(keyValue[0], keyValue[1]);
            }
        }

        return requestBodyAsMap;
    }
}
