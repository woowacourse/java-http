package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.RequestPath;
import nextstep.jwp.model.User;

public class LoginHandler extends AbstractHandler {

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
        String password = queries.get(PASSWORD);
        Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);
        if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
            return redirectMessage(FILE_INDEX_HTML);
        }
        return redirectMessage(FILE_401_HTML);
    }

    private String getMessage(Request request) throws IOException {
        RequestPath requestPath = request.getRequestPath();
        if (requestPath.hasQueryString()) {
            Map<String, String> queries = requestPath.queries();
            String account = queries.get(ACCOUNT);
            String password = queries.get(PASSWORD);
            Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);
            if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
                return redirectMessage(FILE_INDEX_HTML);
            }
            return redirectMessage(FILE_401_HTML);
        }
        final String responseBody = fileByPath(requestPath.path() + HTML_EXTENSION);
        return staticFileMessage(HTML, responseBody);
    }
}
