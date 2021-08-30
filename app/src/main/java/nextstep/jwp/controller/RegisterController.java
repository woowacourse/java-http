package nextstep.jwp.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public RegisterController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(httpRequest.resource());
    }

    @Override
    byte[] post(HttpRequest httpRequest) throws IOException {
        final String[] body = httpRequest.body().split("&");
        final Map<String, String> registerInfo = new LinkedHashMap<>();

        for (String b : body) {
            final String[] split = b.split("=");
            registerInfo.put(split[0].trim(), split[1].trim());
        }
        final String account = registerInfo.get("account");
        final String password = registerInfo.get("password");
        final String email = registerInfo.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return HttpResponse.ok(httpRequest.resource());
        }

        final User user = new User(1, account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.ok(Controller.INDEX_PAGE);
    }

    @Override
    byte[] error(HttpError httpError) throws IOException {
        return new byte[0];
    }
}
