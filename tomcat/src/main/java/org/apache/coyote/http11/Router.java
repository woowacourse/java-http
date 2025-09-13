package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.LoginController;
import org.apache.coyote.http11.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Router {

    private static final Logger log = LoggerFactory.getLogger(Router.class);

    private final Map<String, Controller> controllers = new HashMap<>();
    private final StaticResourceController staticController = new StaticResourceController();

    public Router() {
        // 라우트 등록
        controllers.put("/login", new LoginController());
        controllers.put("/register", new LoginController());
    }

    public void handle(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws Exception {
        try {
    
            String uri = httpRequest.uri();
            Controller controller = findController(uri);

            if (controller != null) {
                controller.service(httpRequest, httpResponse);
                return;
            }
            staticController.service(httpRequest, httpResponse);
        } catch (CommonException e) {
            log.error("exception: ", e);
            httpResponse.setStatusCode(e.getHttpStatus());
            staticController.service(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error("exception: ", e);
            httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            staticController.service(httpRequest, httpResponse);
        }
    }

    private Controller findController(String uri) {
        return controllers.get(uri);
    }

    public void addController(String path, Controller controller) {
        controllers.put(path, controller);
    }
}
