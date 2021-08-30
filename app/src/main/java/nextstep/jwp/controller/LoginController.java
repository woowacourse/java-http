package nextstep.jwp.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final String AND_DELIMITER = "&";
    private static final String EQUAL_DELIMITER = "=";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public LoginController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
        final String uri = httpRequest.uri();
        if (uri.contains("?")) {
            byte[] httpRequest1 = login(httpRequest, uri);
            if (httpRequest1 != null) {
                return httpRequest1;
            }
        }
        return HttpResponse.ok(httpRequest.resource());
    }

    private byte[] login(HttpRequest httpRequest, String uri) throws IOException {
        final String[] split = uri.split(QUERY_STRING_DELIMITER);
        final String[] queryString = split[1].split(AND_DELIMITER);
        final Map<String, String> params = new LinkedHashMap<>();
        for (String s : queryString) {
            final String[] param = s.split(EQUAL_DELIMITER);
            params.put(param[0].trim(), param[1].trim());
        }
        final Optional<User> user = InMemoryUserRepository
                .findByAccount(params.get(ACCOUNT));
        if (user.isPresent()) {
            if (user.get().checkPassword(params.get(PASSWORD))) {
                return HttpResponse.found(httpRequest.resource(Controller.INDEX_PAGE));
            }
        }
        return error(HttpError.UNAUTHORIZED);
    }

    @Override
    public byte[] post(HttpRequest httpRequest) {
        return new byte[0];
    }

    @Override
    public byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(httpError);
    }
}
