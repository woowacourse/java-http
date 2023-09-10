package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;
import javassist.NotFoundException;
import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.catalina.core.util.ResourceReader;
import org.apache.coyote.http11.session.SessionManager.Session;

public class ViewController implements Controller {

    private ViewController() {
    }

    public static HttpServletResponse login(final HttpServletRequest request) throws NotFoundException, IOException {
        return responseByLogin(request.getSession(), "/login.html");
    }

    public static HttpServletResponse register(final HttpServletRequest request) throws NotFoundException, IOException {
        return responseByLogin(request.getSession(), "/register.html");
    }

    private static HttpServletResponse responseByLogin(final Session session, final String redirectUri)
            throws NotFoundException, IOException {
        if (isLoginUser(session)) {
            return HttpServletResponse.redirect("index.html");
        }

        return HttpServletResponse.staticResource(ResourceReader.read(redirectUri));
    }

    private static boolean isLoginUser(final Session session) {
        return Objects.nonNull(session.getAttribute("user"));
    }

}
