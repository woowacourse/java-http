package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.coyote.http11.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;

public class RegisterController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Map<String, Set<String>> requestType = new HashMap<>(Map.of(
                "/register", new HashSet<>(Set.of("GET", "POST"))
        ));

        if (requestType.containsKey(httpRequest.getTarget())) {
            final Set<String> methodType = requestType.get(httpRequest.getTarget());
            return methodType.contains(httpRequest.getMethod());
        }
        return false;
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        handleResource("/register.html", httpRequest, httpResponse);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final User newUser = new User(
                httpRequest.getBody().get("account"),
                httpRequest.getBody().get("password"),
                httpRequest.getBody().get("email")
        );
        InMemoryUserRepository.save(newUser);
        httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponse.addHeader(LOCATION, "/index.html");
        httpResponse.setStatusCode(FOUND);
    }
}
