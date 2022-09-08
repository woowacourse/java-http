package org.apache.coyote.http11.controller.apicontroller;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class RegisterApiController extends AbstractController {

    private static final Pattern REGISTER_URI_PATTERN = Pattern.compile("/register");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchRequestLine(HttpMethod.POST, REGISTER_URI_PATTERN);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        RequestBody requestBody = httpRequest.getRequestBody();

        try {
            Map<String, Object> parameters = requestBody.getParameters();
            String account = (String) parameters.get("account");
            String password = (String) parameters.get("password");
            String email = (String) parameters.get("email");
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            setSession(httpRequest, user);
        } catch (Exception e) {
            httpResponse.sendError();
            return;
        }

        httpResponse.found("/index.html ")
                .addHeader("Set-Cookie", new Cookie(Map.of("JSESSIONID", httpRequest.getSession().getId())))
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                .addHeader("Content-Length", "0 ");
    }

    private void setSession(HttpRequest httpRequest, User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = httpRequest.getSession();
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {

    }
}
