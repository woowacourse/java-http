package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static util.FileLoader.loadFile;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.exception.badrequest.AlreadyRegisterAccountException;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String responseBody = loadFile("/register.html");
        response.statusCode(OK)
                .addHeader(CONTENT_TYPE, ContentType.of(request.getFileExtension()).getValue())
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length)
                .responseBody(responseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpRequestBody httpRequestBody = request.getHttpRequestBody();
        validateRegistrable(httpRequestBody.getBodyValue("account"));
        registerNewUser(httpRequestBody);
        response.statusCode(FOUND)
                .redirect("/index.html");
    }

    private void validateRegistrable(final String account) {
        if (InMemoryUserRepository.existByAccount(account)) {
            throw new AlreadyRegisterAccountException();
        }
    }

    private void registerNewUser(final HttpRequestBody httpRequestBody) {
        final User user = new User(
                httpRequestBody.getBodyValue("account"),
                httpRequestBody.getBodyValue("password"),
                httpRequestBody.getBodyValue("email"));
        InMemoryUserRepository.save(user);
    }
}
