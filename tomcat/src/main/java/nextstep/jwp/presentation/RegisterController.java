package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateUserException;
import nextstep.jwp.exception.EmptyParameterException;
import nextstep.jwp.model.User;
import nextstep.jwp.presentation.dto.UserRegisterRequest;
import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.QueryParameters;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Optional;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        return new HttpResponse(HttpStatus.OK, httpHeaders, ResourceLoader.getContent("register.html"));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final String requestBody = httpRequest.getRequestBody();
        final QueryParameters queryParameters = QueryParameters.from(requestBody);
        final UserRegisterRequest userRegisterRequest = UserRegisterRequest.from(queryParameters);

        try {
            final User user = saveUser(userRegisterRequest);
            log.info("Saved User! user: {}", user);

        } catch (final DuplicateUserException | EmptyParameterException e) {
            return HttpResponse.sendRedirect("/register.html");
        }

        return HttpResponse.sendRedirect("/index.html");
    }

    private User saveUser(final UserRegisterRequest userRegisterRequest) {
        final User user = userRegisterRequest.toEntity();
        validateUserExists(userRegisterRequest.getAccount());
        InMemoryUserRepository.save(user);
        return user;
    }

    private void validateUserExists(final String account) {
        final Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            throw new DuplicateUserException();
        }
    }
}
