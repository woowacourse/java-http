package nextstep.jwp.handler;

import java.util.Map;

import org.apache.coyote.http11.model.RequestParser;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterHandler {

    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public static HttpResponse perform(HttpRequest request) {
        Map<String, String> queries = RequestParser.parseUri(request.getUri());
        if (request.getMethod().isGet() && queries.isEmpty()) {
            return doGet();
        }

        if (request.getMethod().isPost() && queries.isEmpty()) {
            return doPost(request);
        }
        return HttpResponse.notFound();
    }

    private static HttpResponse doGet() {
        return ResourceHandler.returnResource(REGISTER_PAGE);
    }

    private static HttpResponse doPost(HttpRequest request) {
        User user = new User(request.getBodyValue(ACCOUNT), request.getBodyValue(PASSWORD),
                request.getBodyValue(EMAIL));
        InMemoryUserRepository.save(user);
        return HttpResponse.redirect(INDEX_PAGE);
    }
}
