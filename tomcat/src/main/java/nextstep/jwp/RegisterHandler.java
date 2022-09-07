package nextstep.jwp;

import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.util.List;
import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Function<Http11Request, Http11Response> {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);
    private static final String REGISTER_HTML = "/register.html";
    private static final String INDEX_HTML = "/index.html";

    @Override
    public Http11Response apply(final Http11Request request) {
        if (request.isGetMethod()) {
            return Http11Response.of(OK, REGISTER_HTML);
        }

        final List<String> userInfo = List.of("account", "email", "password");
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
        if (request.isPostMethod() && queryParams.hasQueryParams(userInfo)) {
            registerUser(queryParams);
            return Http11Response.withLocation(FOUND, REGISTER_HTML, INDEX_HTML);
        }
        return Http11Response.withLocation(FOUND, REGISTER_HTML, "/404.html");
    }

    private void registerUser(final Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        final String email = queryParams.getValueFrom("email");
        final String password = queryParams.getValueFrom("password");
        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
        log.info(user.toString());
    }
}
