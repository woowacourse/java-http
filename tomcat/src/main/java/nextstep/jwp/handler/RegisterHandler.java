package nextstep.jwp.handler;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;

import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.RequestParser;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterHandler {

    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final int NUM_OF_REGISTER_PARAMETER = 3;

    public static HttpResponse perform(HttpRequest request) {
        Map<String, String> queries = RequestParser.parseUri(request.getUri());
        if (request.getMethod().isGet() && queries.isEmpty()) {
            return staticFileRequest(REGISTER_PAGE);
        }

        if (request.getMethod().isPost() && queries.size() == NUM_OF_REGISTER_PARAMETER && queries.containsKey(ACCOUNT)
                && queries.containsKey(PASSWORD) && queries.containsKey(EMAIL)) {
            User user = new User(queries.get(ACCOUNT), queries.get(PASSWORD), queries.get(EMAIL));
            InMemoryUserRepository.save(user);
            return HttpResponse.redirect(INDEX_PAGE);
        }
        return HttpResponse.notFound();
    }
}
