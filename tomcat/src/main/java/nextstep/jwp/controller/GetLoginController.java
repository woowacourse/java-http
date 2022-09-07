package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Controller;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class GetLoginController implements Controller {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        if (alreadyLogin(httpRequest.getHeader())) {
            return HttpResponse.init(HttpStatusCode.FOUND)
                    .setLocationAsHome();
        }

        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath("/login.html");
    }

    public boolean alreadyLogin(final HttpHeader header) {
        if (!header.hasSessionId()) {
            return false;
        }

        final Session session = SessionManager.findSession(header.getSessionId());
        final User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return httpMethod.isGet() && path.contains("login");
    }
}
