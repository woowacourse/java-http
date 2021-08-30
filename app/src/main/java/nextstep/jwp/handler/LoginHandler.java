package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.model.User;

public class LoginHandler extends AbstractHandler {

    public LoginHandler(Request request) {
        super(request);
    }

    @Override
    public Response getMessage() throws IOException {
        if (request.hasQueryString()) {
            Map<String, String> queries = request.queries();
            String account = queries.get(ACCOUNT);
            String password = queries.get(PASSWORD);
            Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);
            if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
                return new Response(redirectMessage(PathType.INDEX.resource()));
            }
            return new Response(redirectMessage(PathType.UNAUTHORIZED.resource()));
        }
        final String responseBody = fileByPath(request.path() + FileType.HTML.extension());
        return new Response(staticFileMessage(FileType.HTML, responseBody));
    }

    @Override
    public Response postMessage() {
        Map<String, String> queries = request.getRequestBody().queries();
        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);
        Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);
        if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
            return new Response(redirectMessage(PathType.INDEX.resource()));
        }
        return new Response(redirectMessage(PathType.UNAUTHORIZED.resource()));
    }
}
