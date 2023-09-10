package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;
import javassist.NotFoundException;
import org.apache.catalina.core.servlet.ServletResponse;
import org.apache.catalina.core.util.ResourceReader;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.session.SessionManager.Session;

public class ViewController implements Controller {

    private ViewController() {
    }

    public static ServletResponse login(final Request request) throws NotFoundException, IOException {
        return responseByLogin(request.getSession(), "/login.html");
    }

    public static ServletResponse register(final Request request) throws NotFoundException, IOException {
        return responseByLogin(request.getSession(), "/register.html");
    }

    private static ServletResponse responseByLogin(final Session session, final String redirectUri)
            throws NotFoundException, IOException {
        if (isLoginUser(session)) {
            return ServletResponse.redirect("index.html");
        }

        return ServletResponse.staticResource(ResourceReader.read(redirectUri));
    }

    private static boolean isLoginUser(final Session session) {
        return Objects.nonNull(session.getAttribute("user"));
    }

}
