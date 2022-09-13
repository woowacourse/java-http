package nextstep.jwp.controller.handlermapping;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.SessionManager;
import org.apache.catalina.exception.ControllerNotFoundException;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.URL;

public class JwpRequestMapping implements RequestMapping {

    private static final Map<URL, Controller> REQUEST_MAP = new HashMap<>();
    private static final Controller STATIC_CONTROLLER = new StaticFileController();
    private static final RequestMapping INSTANCE = new JwpRequestMapping();

    static {
        REQUEST_MAP.put(URL.of("/login"), new LoginController(SessionManager.getInstance()));
        REQUEST_MAP.put(URL.of("/register"), new RegisterController());
    }

    private JwpRequestMapping() {
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    public Controller map(final URL url) {
        final Controller controller = REQUEST_MAP.get(url);
        if (controller == null) {
            return getStaticController(url);
        }
        return controller;
    }

    private Controller getStaticController(final URL url) {
        if (url.isDefault() || url.isForStaticFile()) {
            return STATIC_CONTROLLER;
        }
        throw new ControllerNotFoundException();
    }
}
