package nextstep.jwp.controller;

import java.io.IOException;
import java.util.NoSuchElementException;
import nextstep.jwp.db.SessionStorage;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController implements Controller {

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }
        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }
        return HttpResponse.notFound();
    }

    private HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        if (httpRequest.containSession()) {
            return HttpResponse.found("/index.html");
        }
        return HttpResponse.ok("/login.html");
    }

    private HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        User user;
        try {
            user = LoginService.login(httpRequest.getBody());
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return HttpResponse.unAuthorized();
        }
        HttpResponse response = HttpResponse.found("/index.html");
        response.addCookie(SessionStorage.getSession(user.getAccount()));
        return response;
    }
}
