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

public class LoginHandler implements Function<Http11Request, Http11Response> {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Http11Response apply(Http11Request request) {
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestUrl());
        if (!queryParams.hasParam()) {
            return Http11Response.of(OK, "/login.html");
        }
        if (isLoginSuccess(queryParams)) {
            return Http11Response.withLocation(FOUND, "/login.html", "/index.html");
        }
        return Http11Response.withLocation(FOUND, "/login.html", "/401.html");
    }

    private boolean isLoginSuccess(Http11QueryParams queryParams) {
        final String account = queryParams.getValueFrom("account");
        final String password = queryParams.getValueFrom("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return true;
        }
        return false;
    }
}
