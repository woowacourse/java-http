package nextstep.jwp.servlet;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestBody;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.Servlet;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class PostRegisterServlet implements Servlet {

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getRequestBody();

        final User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        final Session session = Session.generate();
        session.setAttribute("user", user);
        SessionManager.add(session);

        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .setSessionId(session.getId());
    }

    @Override
    public boolean isMatch(final HttpRequest httpRequest) {
        return httpRequest.isRegister();
    }
}
