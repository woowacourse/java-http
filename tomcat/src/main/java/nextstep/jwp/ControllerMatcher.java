package nextstep.jwp;

import java.io.IOException;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerMatcher {

    private static final String URL_INDEX = "/index.html";
    private static final String URL_LOGIN = "/login";
    private static final String URL_REGISTER = "/register";

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static HttpResponse process(final HttpRequest request) {
        try {
            if (request.getUrl().equals(URL_LOGIN)) {
                return Controller.login(request);
            }
            if (request.getUrl().equals(URL_REGISTER)) {
                return Controller.register(request);
            }
            if (request.getUrl().equals(URL_INDEX)) {
                return Controller.index();
            }
            return Controller.hello();
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return HttpResponse.of(Status.INTERNAL_SERVER_ERROR);
    }
}
