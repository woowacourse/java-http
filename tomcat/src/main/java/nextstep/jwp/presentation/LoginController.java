package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryStrings;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.apache.coyote.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public HttpResponse login(final HttpRequest httpRequest) {
        QueryStrings queryStrings = QueryStrings.of(httpRequest.getUri().getValue());
        String account = queryStrings.find("account");
        String password = queryStrings.find("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("not found account"));

        return response(password, user);
    }

    private HttpResponse response(final String password, final User user) {
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return response("Location: /index.html");
        }
        return response("Location: /401.html");
    }

    private HttpResponse response(final String location) {
        return HttpResponse.builder()
                .version(HttpVersion.HTTP_1_1)
                .status(HttpStatus.FOUND.getValue())
                .headers(location)
                .build();
    }

}
