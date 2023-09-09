package org.apache.coyote.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.FileReader.FileReader;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class LoginController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER_SESSION_KEY = "user";

    @Override
    protected void doGet(Request request, Response response) {
        Session session = request.getSession(false);
        if (session == null || session.getAttribute(USER_SESSION_KEY) == null) {
            FileReader fileReader = FileReader.from(request.getPath());
            String body = fileReader.read();

            response.setHttpStatus(HttpStatus.OK);
            response.addHeaders(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
            response.addHeaders(CONTENT_TYPE, request.getResourceTypes());
            response.setResponseBody(body);
            return;
        }
        response.setHttpStatus(HttpStatus.FOUND);
        response.addHeaders(CONTENT_TYPE, request.getResourceTypes());
        response.redirectLocation(INDEX_PATH);
    }

    @Override
    protected void doPost(Request request, Response response) {
        if (request.getSession(false) != null) {
            Session session = request.getSession(false);
            User user = (User) session.getAttribute(USER_SESSION_KEY);
            System.out.println(user);
            response.setHttpStatus(HttpStatus.FOUND);
            response.redirectLocation(INDEX_PATH);
            return;
        }
        Map<String, String> requestBody = request.getBody();
        String account = requestBody.get(ACCOUNT);
        String password = requestBody.get(PASSWORD);

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
        if (loginUser.isEmpty() || !loginUser.get().checkPassword(password)) {
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.redirectLocation(UNAUTHORIZED_PATH);
            return;
        }
        User user = loginUser.get();
        System.out.println(user);

        Session session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user);

        response.addCookie(JSESSIONID, session.getId());
        response.setHttpStatus(HttpStatus.FOUND);
        response.redirectLocation(INDEX_PATH);
    }
}
