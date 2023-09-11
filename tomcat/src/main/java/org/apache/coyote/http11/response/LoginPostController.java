package org.apache.coyote.http11.response;

import nextstep.jwp.LoginHandler;
import org.apache.catalina.session.HttpSession;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginPostController implements Controller {

    @Override
    public void service(final HttpRequest request,
                        final HttpResponse response) {
        final LoginHandler loginHandler = new LoginHandler();
        if (loginHandler.login(request.getRequestBody())) {
            successLoginResponse(request, response);
            return;
        }
        failLoginResponse(response);
    }

    private void successLoginResponse(final HttpRequest request,
                                      final HttpResponse response) {
        response.setStatusCode(StatusCode.FOUND);

        if (!request.hasJSessionId()) {
            final HttpSession httpSession = new HttpSession();
            final LoginHandler loginHandler = new LoginHandler();
            httpSession.addAttribute("user", loginHandler.getUser(request.getRequestBody()));
            httpSession.addAttribute(httpSession.getId(), httpSession);

            response.addJSessionId(httpSession.getId());
        }
        response.addHeader("Location", "/index.html");
    }

    private void failLoginResponse(final HttpResponse response) {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setResponseBody(ResourceResolver.resolve("/401.html"));
        response.addHeader("Content-Type", ContentType.from("/401.html").getContentType() + ";charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(ResourceResolver.resolve("/401.html").getBytes().length));
    }

}
