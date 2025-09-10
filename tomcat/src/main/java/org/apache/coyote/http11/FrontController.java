package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticFileController;
import java.util.List;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.exception.DataNotFoundException;
import org.apache.coyote.exception.InvalidRequestException;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.value.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final List<Controller> controllers = List.of(
            new LoginController(),
            new RegisterController(),
            new RootController(),
            new StaticFileController()
    );

    public void service(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = getController(request);
            controller.service(request, response);
        } catch (InvalidRequestException e) {
            log.info(e.getMessage(), e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setRedirection("/400.html");
        } catch (UnauthorizedException e) {
            log.info(e.getMessage(), e);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setRedirection("/401.html");
        } catch (DataNotFoundException e) {
            log.info(e.getMessage(), e);
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setRedirection("/404.html");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatusCode(StatusCode.INTERNAL_SERVER_ERROR);
            response.setRedirection("/500.html");
        }
    }

    public Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canProcessable(request))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("유효하지 않은 요청입니다."));
    }
}
