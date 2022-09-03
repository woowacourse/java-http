package nextstep.jwp;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.model.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static final String URL_LOGIN = "/login";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_PASSWORD = "password";

    public static void process(final Request request) {
        if (isLoginRequest(request)) {
            login(request);
        }
    }

    private static boolean isLoginRequest(final Request request) {
        return request.getUrl().startsWith(URL_LOGIN) && !request.getQueryParams().isEmpty();
    }

    private static void login(final Request request) {
        Map<String, String> queryParams = request.getQueryParams();

        User user = InMemoryUserRepository.findByAccount(queryParams.get(KEY_ACCOUNT))
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(queryParams.get(KEY_PASSWORD))) {
            log.info(user.toString());
        }
    }
}
