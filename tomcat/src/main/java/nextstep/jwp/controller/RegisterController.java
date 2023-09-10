package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;

public class RegisterController extends AbstractController {

    private static final String SUPPORT_URI_PATH = "/register";
    private static final String INDEX_FILE_PATH = "/index.html";
    private static final String REGISTER_FILE_PATH = "/register.html";

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathOf(SUPPORT_URI_PATH);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.search("account");
        final String password = requestBody.search("password");
        final String email = requestBody.search("email");
        final User user = new User(account, password, email);

        try {
            InMemoryUserRepository.save(user);
        } catch (IllegalArgumentException e) {
            final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.BAD_REQUEST, REGISTER_FILE_PATH);
            response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
        }

        final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.FOUND, INDEX_FILE_PATH);
        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.OK, REGISTER_FILE_PATH);

        response.setResponse(HttpResponse.of(request.getHttpVersion(), responseEntity));
    }
}
