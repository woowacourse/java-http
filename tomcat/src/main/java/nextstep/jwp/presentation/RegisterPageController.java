package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.util.ResourceReader;

import java.util.UUID;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.OK;

public class RegisterPageController extends AbstractController {

    private static final String REGISTER_SUCCESS_REDIRECT_URI = "/index.html";
    private static final String REGISTER_PAGE_URI = "/register.html";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final RequestBody requestBody = request.requestBody();
        final String account = requestBody.getBodyValue("account");
        final String password = requestBody.getBodyValue("password");
        final String email = requestBody.getBodyValue("email");

        InMemoryUserRepository.save(new User(account, password, email));

        final Session newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute("account", account);
        SessionManager.add(newSession);

        response.setHttpVersion(HTTP_1_1)
                .setCookies(Cookies.ofJSessionId(newSession.id()))
                .sendRedirect(REGISTER_SUCCESS_REDIRECT_URI);
    }


    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String registerPageResource = ResourceReader.read(REGISTER_PAGE_URI);
        final ResponseBody registerPageBody = ResponseBody.from(registerPageResource);

        response.setHttpVersion(HTTP_1_1)
                .setHttpStatus(OK)
                .setContentType(TEXT_HTML.value())
                .setContentLength(registerPageBody.length())
                .setResponseBody(registerPageBody);
    }
}
