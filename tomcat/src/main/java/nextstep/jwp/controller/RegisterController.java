package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String CONFLICT_PAGE = "/409.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get(ACCOUNT);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.setHttpStatus(HttpStatus.CONFLICT)
                    .sendRedirect(CONFLICT_PAGE);
            return;
        }

        final String password = requestBody.get(PASSWORD);
        final String email = requestBody.get(EMAIL);
        InMemoryUserRepository.save(new User(account, password, email));
        response.setHttpStatus(HttpStatus.FOUND)
                .addHeader("Location", INDEX_PAGE)
                .sendRedirect(INDEX_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setHttpStatus(HttpStatus.OK)
                .sendRedirect(REGISTER_PAGE);
    }
}
