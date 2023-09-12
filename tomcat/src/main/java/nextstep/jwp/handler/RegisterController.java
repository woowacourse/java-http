package nextstep.jwp.handler;

import nextstep.jwp.exception.AlreadyRegisteredUserException;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.exception.NoSuchBodyValueException;
import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.util.ResourceFileReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_BODY = "account";
    private static final String EMAIL_BODY = "email";
    private static final String PASSWORD_BODY = "password";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        setResponse(response, HttpStatus.OK, "/register.html");
    }

    private void setResponse(final HttpResponse response, final HttpStatus status, final String bodyPath) {
        final String body = ResourceFileReader.readFile(bodyPath);
        response.setStatus(status)
                .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                .setBody(body);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        try {
            authService.register(
                    request.getBodyValueOf(ACCOUNT_BODY),
                    request.getBodyValueOf(PASSWORD_BODY),
                    request.getBodyValueOf(EMAIL_BODY)
            );

            response.setStatus(HttpStatus.REDIRECT)
                    .setHeader(HttpHeader.LOCATION, "/login");

        } catch (AlreadyRegisteredUserException | NoSuchBodyValueException e) {
            setResponse(response, HttpStatus.BAD_REQUEST, "/400.html");
        }
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isUrl(Url.from("/register"));
    }
}
