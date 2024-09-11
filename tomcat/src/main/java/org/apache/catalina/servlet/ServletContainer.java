package org.apache.catalina.servlet;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Set;
import org.apache.catalina.exception.CatalinaException;
import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;

public class ServletContainer {

    private static final ServletContainer INSTANCE = new ServletContainer();

    private final Set<Controller> controllers = Set.of(
            new LoginController(),
            new RegisterController()
    );

    private final Controller resourceController = new ResourceController();

    private ServletContainer() {
    }

    public static ServletContainer getInstance() {
        return INSTANCE;
    }

    public void service(HttpRequest request, HttpResponse response) {
        Controller controller = getController(request);
        controller.service(request, response);
        validateResponse(response);
    }

    private Controller getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller ->
                        request.URIStartsWith(controller.getClass().getAnnotation(WebServlet.class).value())
                )
                .findAny()
                .orElse(resourceController);
    }

    private void validateResponse(HttpResponse response) {
        if (!response.isValid()) {
            throw new CatalinaException("Response not valid: \n" + response);
        }
    }
}
