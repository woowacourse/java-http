package nextstep.jwp.presentation;

import static org.apache.coyote.http11.support.HttpHeader.LOCATION;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.EmptyParameterException;
import nextstep.jwp.exception.PasswordNotMatchException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.QueryParameters;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public HttpResponse login(final QueryParameters queryParameters) {
        try {
            validateQueryParametersExist(queryParameters);
            final String account = queryParameters.getValueByKey("account");
            final String password = queryParameters.getValueByKey("password");
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(UserNotFoundException::new);
            log.info("user: {}", user);
            validatePassword(user, password);

            final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
            httpHeaders.put(LOCATION, "/index.html");

            return new HttpResponse(HttpStatus.FOUND, httpHeaders, "");

        } catch (EmptyParameterException e) {
            final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
            httpHeaders.put(LOCATION, "/login.html");

            return new HttpResponse(HttpStatus.FOUND, httpHeaders, "");

        } catch (UserNotFoundException | PasswordNotMatchException e) {
            final String uri = "/401.html";
            final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
            httpHeaders.put(LOCATION, uri);

            return new HttpResponse(HttpStatus.FOUND, httpHeaders, "");
        }
    }

    private void validateQueryParametersExist(final QueryParameters queryParameters) {
        if (queryParameters.isEmpty()) {
            throw new EmptyParameterException();
        }
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new PasswordNotMatchException();
        }
    }
}
