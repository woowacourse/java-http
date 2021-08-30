package nextstep.jwp.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.FileReader;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpError;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String AMPERSAND_DELIMITER = "&";
    private static final String EQUAL_DELIMITER = "=";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public LoginController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(
                FileReader.file(httpRequest.uri()),
                ContentType.findBy(httpRequest.uri())
        );
    }

    @Override
    public byte[] post(HttpRequest httpRequest) throws IOException {
        final String[] body = httpRequest.body().split(AMPERSAND_DELIMITER);
        final Map<String, String> loginInfo = getRequestBody(body);

        final Optional<User> user = InMemoryUserRepository
                .findByAccount(loginInfo.get(ACCOUNT));

        if (user.isPresent()) {
            if (user.get().checkPassword(loginInfo.get(PASSWORD))) {
                return HttpResponse.found(
                        FileReader.file(Controller.INDEX_PAGE),
                        ContentType.findBy(Controller.INDEX_PAGE)
                );
            }
        }
        return HttpResponse.ok(
                FileReader.file(httpRequest.uri()),
                ContentType.findBy(httpRequest.uri())
        );
    }

    private Map<String, String> getRequestBody(String[] body) {
        final Map<String, String> registerInfo = new LinkedHashMap<>();
        for (String b : body) {
            final String[] split = b.split(EQUAL_DELIMITER);
            registerInfo.put(split[0].trim(), split[1].trim());
        }
        return registerInfo;
    }

    @Override
    public byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(httpError);
    }
}
