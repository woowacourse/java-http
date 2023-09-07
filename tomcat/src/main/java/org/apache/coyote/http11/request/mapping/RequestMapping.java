package org.apache.coyote.http11.request.mapping;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.FileExtension;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomeController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;

public class RequestMapping {

    private static final Map<String, Controller> requestMapingMap = new HashMap<>();

    static {
        requestMapingMap.put("/", new HomeController());
        requestMapingMap.put("/login", new LoginController());
        requestMapingMap.put("/register", new RegisterController());
    }

    private RequestMapping() {
    }

    public static Controller getController(final HttpRequest request) {
        String mappingUri = request.getMappingUri();
        if (FileExtension.isContains(mappingUri)) {
            return new ResourceController();
        }

        for (String uri : requestMapingMap.keySet()) {
            if (uri.equals(mappingUri)) {
                return requestMapingMap.get(uri);
            }
        }
        throw new HttpRequestException.NotFoundMappingController();
    }
}
