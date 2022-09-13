package nextstep.jwp.support;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nextstep.jwp.exception.NotFoundControllerException;
import nextstep.jwp.ui.DefaultController;
import nextstep.jwp.ui.IndexController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import nextstep.jwp.ui.ResourceController;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.support.Controller;
import org.apache.coyote.support.RequestMapping;

public class JwpRequestMapping implements RequestMapping {

    private final static Map<Predicate<String>, Controller> CONTROLLER = Map.of(
            "/"::equals, new DefaultController(),
            "/index.html"::equals, new IndexController(),
            "/login"::equals, new LoginController(),
            "/register"::equals, new RegisterController(),
            JwpRequestMapping::isStaticResource, new ResourceController()
    );
    private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile(".*\\..*");

    private static boolean isStaticResource(final String url) {
        final Matcher matcher = STATIC_RESOURCE_PATTERN.matcher(url);
        return matcher.find();
    }

    @Override
    public Controller getController(Http11Request request) {
        return CONTROLLER.entrySet().stream()
                .filter(entry -> entry.getKey().test(request.getRequestUrl()))
                .findAny()
                .map(Entry::getValue)
                .orElseThrow(NotFoundControllerException::new);
    }
}
