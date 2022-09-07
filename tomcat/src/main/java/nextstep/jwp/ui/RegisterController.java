package nextstep.jwp.ui;

import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.support.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_HTML = "/register.html";
    private static final String INDEX_HTML = "/index.html";

    @Override
    protected Http11Response doGet(final Http11Request request) {
        return Http11Response.of(OK, REGISTER_HTML);
    }

    @Override
    protected Http11Response doPost(final Http11Request request) {
        final List<String> userInfo = List.of("account", "email", "password");
        final Http11QueryParams queryParams = Http11QueryParams.from(request.getRequestBody());
        if (queryParams.hasQueryParams(userInfo)) {
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
