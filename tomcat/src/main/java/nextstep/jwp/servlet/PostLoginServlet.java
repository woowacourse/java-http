package nextstep.jwp.servlet;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestBody;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Servlet;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class PostLoginServlet implements Servlet {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getRequestBody();
        final String account = requestBody.get("account");
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(account);

        if (possibleUser.isEmpty()) {
            return HttpResponse.init(HttpStatusCode.UNAUTHORIZED)
                    .setBodyByPath("/401.html");
        }

        final Session session = Session.generate();
        session.setAttribute("user", possibleUser.get());
        SessionManager.add(session);

        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .setSessionId(session.getId());
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return httpMethod.isPost() && path.contains("login");
    }
}
