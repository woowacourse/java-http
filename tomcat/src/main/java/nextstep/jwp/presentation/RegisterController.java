package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateUserException;
import nextstep.jwp.exception.EmptyParameterException;
import nextstep.jwp.model.User;
import nextstep.jwp.presentation.dto.UserRegisterRequest;
import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Optional;

public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public HttpResponse render() throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("register.html"));
    }

    public HttpResponse register(final UserRegisterRequest userRegisterRequest) {
        log.info("userRegisterRequest: {}", userRegisterRequest);
        try {
            final User user = userRegisterRequest.toEntity();
            validateUserExists(userRegisterRequest.getAccount());
            InMemoryUserRepository.save(user);
            log.info("Saved User! user: {}", user);
        } catch (final DuplicateUserException | EmptyParameterException e) {
            log.error("message: {}", e.getMessage());
            final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
            httpHeaders.put(HttpHeader.LOCATION, "/register.html");
            return new HttpResponse(HttpStatus.FOUND, httpHeaders, "");
        }

        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.LOCATION, "/index.html");
        return new HttpResponse(HttpStatus.FOUND, httpHeaders, "");
    }

    private void validateUserExists(final String account) {
        final Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            throw new DuplicateUserException();
        }
    }
}
