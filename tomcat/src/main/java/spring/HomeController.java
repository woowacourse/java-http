package spring;

import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final Controllers controllers;

    public HomeController(final Controllers controllers) {
        this.controllers = controllers;
    }

    public String service(final HttpRequest request) {
        log.info("request parameters : {}", request.getParameters());
        log.info("request body : {}", request.getBody());

        var requestURI = request.getRequestURIWithoutQueryParams();
        final var controller = controllers.findController(requestURI);

        return controller.service(request);
    }
}
