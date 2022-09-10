package nextstep.jwp.config;

import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.StaticResourceController;
import nextstep.jwp.presentation.WelcomeController;
import org.apache.coyote.Controller;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.http11.common.HttpRequest;

public class JwpRequestMapping implements RequestMapping {

    private final Map<String, Controller> requestMapping;

    public JwpRequestMapping() {
        this.requestMapping = Map.of(
                "/", WelcomeController.getInstance(),
                "/login", LoginController.getInstance(),
                "/register", RegisterController.getInstance()
        );
    }

    public Controller findController(final HttpRequest request) {
        return requestMapping.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(request.getPath()))
                .map(Entry::getValue)
                .findFirst()
                .orElse(StaticResourceController.getInstance());
    }
}
