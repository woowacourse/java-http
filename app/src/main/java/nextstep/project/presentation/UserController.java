package nextstep.project.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.HttpStatus;
import nextstep.project.db.InMemoryUserRepository;
import nextstep.project.model.User;

public class UserController extends HttpHandler {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doGet(httpRequest, httpResponse);

        try {
            Map<String, String> queryString = httpRequest.getQueryStringAsMap();

            if (queryString.isEmpty()) {
                throw new NoSuchElementException();
            }

            String requestAccount = queryString.get("account");
            String requestPassword = queryString.get("password");

            User findUser = InMemoryUserRepository.findByAccount(requestAccount)
                .orElseThrow(UnauthorizedException::new);

            if (!findUser.checkPassword(requestPassword)) {
                throw new UnauthorizedException();
            }

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

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doPost(httpRequest, httpResponse);
        try {
            Map<String, String> body = parseFormData(httpRequest.getBody());

            String requestAccount = body.get("account");
            String requestPassword = body.get("password");

            User findUser = InMemoryUserRepository.findByAccount(requestAccount)
                .orElseThrow(UnauthorizedException::new);

            if (!findUser.checkPassword(requestPassword)) {
                throw new UnauthorizedException();
            }

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
