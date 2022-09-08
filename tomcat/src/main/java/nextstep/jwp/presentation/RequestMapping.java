package nextstep.jwp.presentation;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.utils.UrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMapping {
    private static final Logger log = LoggerFactory.getLogger(RequestMapping.class);
    private static final Map<String, Controller> HANDLER_MAPPING;

    static {
        HANDLER_MAPPING = Map.of(
                "login", new LoginController(),
                "register", new RegisterController(),
                "", new RootController());
    }

    public static Controller getController(HttpRequest request) {
        log.info("request : {}", request);
        if (isHomeUrl(request.getPath())) {
            return new HomeController();
        }
        String path = UrlParser.extractOnlyFile(request);
        return HANDLER_MAPPING.keySet().stream()
                .filter(path::equals)
                .map(HANDLER_MAPPING::get)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 컨트롤러가 없습니다."));
    }

    private static boolean isHomeUrl(String uri) {
        return uri.startsWith("/index") || uri.endsWith("css") || uri.endsWith("csv") || uri.endsWith("js")
                || uri.endsWith("ico");
    }
}
