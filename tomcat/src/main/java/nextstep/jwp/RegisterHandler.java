package nextstep.jwp;

import static org.apache.coyote.http11.StatusCode.FOUND;
import static org.apache.coyote.http11.StatusCode.OK;

import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements Function<Http11Request, Http11Response> {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    public Http11Response apply(Http11Request request) {
        if (request.isGetMethod()) {
            return Http11Response.of(OK, "/register.html");
        }
        if (request.isPostMethod()) {
            final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
            registerUser(queryParams);
            return Http11Response.withLocation(FOUND, "/register.html", "/index.html");
        }
        return Http11Response.withLocation(FOUND, "/register.html", "/404.html");
    }

    private void registerUser(Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        final String email = queryParams.getValueFrom("email");
        final String password = queryParams.getValueFrom("password");
        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
        log.info(user.toString());
    }
}
