package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.model.Parameters;
import org.apache.coyote.http11.model.Path;
import org.apache.coyote.http11.model.View;
import org.apache.coyote.http11.utils.Files;

public final class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final Parameters loginParameters = Parameters.parseParameters(request.getRequestBody(), "&");
        final String account = loginParameters.get("account");
        final String password = loginParameters.get("password");
        final String email = loginParameters.get("email");

        if (InMemoryUserRepository.existsByAccount(account)) {
            log.info("이미 등록된 Account입니다. [account : " + account + "]");
            return HttpResponse.found(View.BAD_REQUEST.getPath());
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.found(View.INDEX.getPath());
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String path = Path.from(request.getPath());
        final String body = Files.readFile(path);
        return HttpResponse.ok(body);
    }
}
