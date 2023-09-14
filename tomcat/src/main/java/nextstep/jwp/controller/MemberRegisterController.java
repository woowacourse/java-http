package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberRegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(MemberRegisterController.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    void doGet(final HttpRequest httpRequest, final Http11Response httpResponse) {
        registerPage(httpResponse);
    }

    @Override
    void doPost(final HttpRequest httpRequest, final Http11Response httpResponse) {
        final RequestBody body = httpRequest.getRequestBody();

        final String account = body.getByKey(ACCOUNT);
        final String password = body.getByKey(PASSWORD);
        final String email = body.getByKey(EMAIL);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("User create - {}", user);

        redirectIndexPage(httpResponse);
    }

    private void redirectIndexPage(final Http11Response httpResponse) {
        final String resourcePath = RESOURCE_PATH + INDEX_PAGE;
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(302);
        httpResponse.setStatusMessage(HTTP_FOUND);
    }

    private void registerPage(final Http11Response httpResponse) {
        final String resourcePath = RESOURCE_PATH + "/register.html";
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(200);
        httpResponse.setStatusMessage("OK");
    }
}
