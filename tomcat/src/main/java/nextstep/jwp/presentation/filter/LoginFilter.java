package nextstep.jwp.presentation.filter;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;
import static org.apache.coyote.http11.util.HttpMethod.POST;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.dto.request.LoginRequest;
import nextstep.jwp.model.User;
import nextstep.jwp.presentation.resolver.FormDataResolver;
import org.apache.coyote.http11.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);

    public void doFilter(final HttpRequest request) {
        final Map<String, String> params = FormDataResolver.resolve(request.getRequestBody());
        final LoginRequest loginRequest = LoginRequest.from(params);
        final Optional<User> user = findByAccount(loginRequest.getAccount());
        if (user.isPresent()) {
            if (user.get().checkPassword(loginRequest.getPassword())) {
                log.info(user.get().toString());
            }
        }
    }

    public boolean support(final HttpRequest request) {
        final var requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("login") && request.getHttpMethod() == POST;
    }
}
