package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticFileController;
import java.util.List;
import org.apache.catalina.container.controller.Controller;
import org.apache.catalina.container.exception.DataNotFoundException;
import org.apache.catalina.container.exception.InvalidRequestException;
import org.apache.catalina.container.exception.UnauthorizedException;
import org.apache.catalina.container.http.request.HttpRequest;
import org.apache.catalina.container.http.response.HttpResponse;
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
            response.setRedirection("/400.html");
        } catch (UnauthorizedException e) {
            log.info(e.getMessage(), e);
            response.setRedirection("/401.html");
        } catch (DataNotFoundException e) {
            log.info(e.getMessage(), e);
            response.setRedirection("/404.html");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
