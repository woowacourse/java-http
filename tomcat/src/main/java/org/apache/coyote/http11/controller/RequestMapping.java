package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.ResponseLine;

public enum RequestMapping {

    INSTANCE;

    private static final Map<String, Controller> CONTROLLERS = new ConcurrentHashMap<>();

    static {
        CONTROLLERS.put("/", DefaultController.getInstance());
        CONTROLLERS.put("/index", ResourceController.getInstance());
        CONTROLLERS.put("/login", LoginController.getInstance());
        CONTROLLERS.put("/register", RegisterController.getInstance());
        CONTROLLERS.put("/401", UnauthorizedController.getInstance());
    }

    public HttpResponse extractHttpResponse(HttpRequest request) {
        ResponseEntity responseEntity = service(request);
        File viewFile = ViewResolver.findViewFile(responseEntity.getPath());

        return new HttpResponse(
                new ResponseLine(responseEntity.getHttpStatus()),
                ResponseHeaders.from(responseEntity, MimeType.from(viewFile)),
                new ResponseBody(readString(viewFile))
        );
    }

    private ResponseEntity service(HttpRequest request) {
        try {
            String path = request.getRequestLine().getPath();
            Controller controller = getController(path);

            return controller.service(request);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .path("/500")
                    .build();
        }
    }

    private Controller getController(String path) {
        if (path.contains(".")) {
            return ResourceController.getInstance();
        }
        return CONTROLLERS.getOrDefault(path, NotFoundController.getInstance());
    }

    private String readString(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException | NullPointerException e) {
            return "";
        }
    }

}
