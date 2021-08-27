package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.RequestPath;
import nextstep.jwp.model.User;

public class RegisterHandler extends AbstractHandler {

    @Override
    public String message(Request request) throws IOException {
        if (GET.equals(request.getRequestMethod())) {
            return getMessage(request);
        }
        return postMessage(request);
    }

    private String postMessage(Request request) {
        Map<String, String> queries = request.getRequestBody().queries();
        String account = queries.get(ACCOUNT);
        String email = queries.get(EMAIL);
        String password = queries.get(PASSWORD);
        if (InMemoryUserRepository.existsByAccount(account)
                || InMemoryUserRepository.existsByEmail(email)) {
            return redirectMessage("/401.html");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return redirectMessage("/index.html");
    }

    private String getMessage(Request request) throws IOException {
        RequestPath requestPath = request.getRequestPath();
        final String responseBody = fileByPath(requestPath.path() + ".html");
        return staticFileMessage(HTML, responseBody);
    }
}
