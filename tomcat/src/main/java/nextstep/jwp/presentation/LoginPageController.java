package nextstep.jwp.presentation;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.util.ResourceReader;

import java.util.Objects;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.OK;

public class LoginPageController extends AbstractController {

    private static final String LOGIN_PAGE_URI = "/login.html";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "/index.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isAuthenticated(request)) {
            response.setHttpVersion(HTTP_1_1)
                    .sendRedirect(LOGIN_SUCCESS_REDIRECT_URI)
                    .setResponseBody(ResponseBody.empty());

            return;
        }

        final String loginPageResource = ResourceReader.read(LOGIN_PAGE_URI);
        final ResponseBody loginPageBody = ResponseBody.from(loginPageResource);

        response.setHttpVersion(HTTP_1_1)
                .setHttpStatus(OK)
                .setContentType(TEXT_HTML.value())
                .setContentLength(loginPageBody.length())
                .setResponseBody(loginPageBody);
    }

    private boolean isAuthenticated(final HttpRequest httpRequest) {
        final String jsessionid = httpRequest.getCookieValue("JSESSIONID");
        System.out.println("jsessionid = " + jsessionid);
        final Session foundSession = SessionManager.findSession(jsessionid);
        System.out.println("foundSession = " + foundSession);

        return Objects.nonNull(foundSession);
    }
}
