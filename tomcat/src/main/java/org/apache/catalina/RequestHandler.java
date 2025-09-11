package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final RequestMapping requestMapping = new RequestMapping();
    private final StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

    public HttpResponse handle(HttpRequest request) throws IOException {
        Controller controller = requestMapping.getController(request);
        try {
            if (controller != null) {
                return controller.service(request);
            }
            return staticResourceHandler.service(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            byte[] body = get500Page();
            return HttpResponse.internalServerError()
                    .contentType(ContentType.TEXT_HTML)
                    .contentLength(body.length)
                    .body(body)
                    .build();
        }
    }

    private byte[] get500Page() throws IOException {
        URL resourceURL = getClass().getClassLoader().getResource("static/500.html");
        Path path = Path.of(resourceURL.getFile());
        return Files.readAllBytes(path);
    }
}
