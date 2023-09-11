package nextstep.jwp;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.FileExtractor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;

public class RegisterController extends AbstractController {

    private static final String BAD_REQUEST = "/400";
    private static final String CONFLICT = "/409";
    private static final String INDEX = "/index";
    private static final String REGISTER = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final Map<String, String> requestBody = request.getRequestBody();

        final String account = requestBody.get(ACCOUNT);
        final String password = requestBody.get(PASSWORD);
        final String email = requestBody.get(EMAIL);

        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            final ResponseBody responseBody = FileExtractor.extractFile(BAD_REQUEST);
            final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

            response.setStatusLine(new StatusLine(HttpStatusCode.BAD_REQUEST));
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
            return;
        }

        if (InMemoryUserRepository.checkExistingId(account)) {
            final ResponseBody responseBody = FileExtractor.extractFile(CONFLICT);
            final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

            response.setStatusLine(new StatusLine(HttpStatusCode.CONFLICT));
            response.setResponseHeader(responseHeader);
            response.setResponseBody(responseBody);
            return;
        }
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        final ResponseBody responseBody = FileExtractor.extractFile(INDEX);
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

        response.setStatusLine(new StatusLine(HttpStatusCode.OK));
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final ResponseBody responseBody = FileExtractor.extractFile(REGISTER);
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

        response.setStatusLine(new StatusLine(HttpStatusCode.OK));
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
    }
}
