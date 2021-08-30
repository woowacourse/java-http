package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;
import nextstep.jwp.model.User;

public class RegisterHandler extends AbstractHandler {

    public RegisterHandler(Request request) {
        super(request);
    }

    @Override
    public Response getMessage() throws IOException {
        final String responseBody = fileByPath(request.path() + FileType.HTML.extension());
        return new Response(staticFileMessage(FileType.HTML, responseBody));
    }

    @Override
    public Response postMessage() {
        Map<String, String> queries = request.getRequestBody().queries();
        String account = queries.get(ACCOUNT);
        String email = queries.get(EMAIL);
        String password = queries.get(PASSWORD);
        if (InMemoryUserRepository.existsByAccount(account)
                || InMemoryUserRepository.existsByEmail(email)) {
            return new Response(redirectMessage(PathType.UNAUTHORIZED.resource()));
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return new Response(redirectMessage(PathType.INDEX.resource()));
    }
}
