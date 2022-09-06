package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public HttpResponse login(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        User user = InMemoryUserRepository.findByAccount(values.get("account"))
                .orElseThrow(() -> new RuntimeException("not found account"));

        return response(values.get("password"), user);
    }

    public HttpResponse register(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        String account = values.get("account");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            return response("Location: /401.html");
        }
        User user = new User(account, values.get("password"), values.get("email"));

        InMemoryUserRepository.save(user);
        return response("Location: /index.html");
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
