package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.RequestPath;
import nextstep.jwp.model.User;

public class RegisterHandler extends AbstractHandler {

    @Override
    public String getMessage(Request request) throws IOException {
        RequestPath requestPath = request.getRequestPath();
        final String responseBody = fileByPath(requestPath.path() + HTML_EXTENSION);
        return staticFileMessage(HTML, responseBody);
    }

    @Override
    public String postMessage(Request request) {
        Map<String, String> queries = request.getRequestBody().queries();
        String account = queries.get(ACCOUNT);
        String email = queries.get(EMAIL);
        String password = queries.get(PASSWORD);
        if (InMemoryUserRepository.existsByAccount(account)
                || InMemoryUserRepository.existsByEmail(email)) {
            return redirectMessage(FILE_401_HTML);
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return redirectMessage(FILE_INDEX_HTML);
    }
}
