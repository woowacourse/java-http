package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class LoginController implements Controller {

    @Override
    public String run(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isEmptyQueryString()) {
            httpResponse.setStatusCode(HttpStatusCode.OK);
            return "/login.html";
        }
        final String account = httpRequest.getParameter("account");
        final String password = httpRequest.getParameter("password");
        final boolean isSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
        if (isSuccess) {
            httpResponse.setStatusCode(HttpStatusCode.FOUND);
            httpResponse.setHeader("Location", "/");
            return "/index.html";
        }
        httpResponse.setStatusCode(HttpStatusCode.FOUND);
        httpResponse.setHeader("Location", "/401.html");
        return "/401.html";
    }
}
