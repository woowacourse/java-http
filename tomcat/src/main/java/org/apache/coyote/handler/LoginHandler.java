package org.apache.coyote.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.FileReader.FileReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class LoginHandler {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public void login(Request request, Response response) {
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            Map<String, String> requestBody = request.getBody();
            String account = requestBody.get(ACCOUNT);
            String password = requestBody.get(PASSWORD);

            Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
            if (loginUser.isEmpty() || !loginUser.get().checkPassword(password)) {
                response.setHttpStatus(HttpStatus.UNAUTHORIZED);
                response.redirectLocation("/401.html");
                return;
            }
            User user = loginUser.get();
            System.out.println(user);

            Session session = request.getSession(true);
            session.setAttribute("user", user);

            response.setHttpStatus(HttpStatus.FOUND);
            response.addCookie(JSESSIONID, session.getId());
            response.redirectLocation("/index.html");
        }
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            Session session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                String path = request.getPath();
                FileReader fileReader = FileReader.from(path);
                String body = fileReader.read();
                response.setHttpStatus(HttpStatus.OK);
                response.addHeaders("Content-Type", request.getResourceTypes());
                response.addHeaders("Content-Length", String.valueOf(body.getBytes().length));
                response.setResponseBody(body);
                return;
            }
            response.addHeaders("Content-Type", request.getResourceTypes());
            response.setHttpStatus(HttpStatus.FOUND);
            response.redirectLocation("/index.html");
        }
    }
}
