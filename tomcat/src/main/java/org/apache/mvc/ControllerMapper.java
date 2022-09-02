package org.apache.mvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.exception.TempException;
import org.apache.mvc.annotation.RequestMapping;

public class ControllerMapper {

    private static final String REDIRECT = "redirect:";
    private final Map<RequestKey, RequestHandler> map;

    private ControllerMapper(Map<RequestKey, RequestHandler> map) {
        this.map = map;
    }

    public static ControllerMapper from(List<Controller> controllers) {
        Map<RequestKey, RequestHandler> map = controllers.stream()
                .flatMap(controller -> dismantleMethod(controller).entrySet().stream())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        (k, v) -> {
                            throw new TempException();
                        }
                ));
        return new ControllerMapper(map);
    }

    private static Map<RequestKey, RequestHandler> dismantleMethod(Controller controller) {
        return Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(method -> findRequestMappedMethod(method) != null)
                .collect(Collectors.toMap(
                        method -> RequestKey.from(findRequestMappedMethod(method)),
                        method -> new RequestHandler(controller, method)
                ));
    }

    private static RequestMapping findRequestMappedMethod(Method method) {
        return method.getDeclaredAnnotation(RequestMapping.class);
    }

    public ResponseEntity mapToHandle(HttpRequest httpRequest) {
        RequestHandler requestHandler = findMappedHandler(httpRequest);
        if (requestHandler == null) {
            return mapToStatic(httpRequest.getPath());
        }
        ResponseEntity entity = requestHandler.handle(httpRequest);
        return handleIfRedirect(entity);
    }

    private ResponseEntity handleIfRedirect(ResponseEntity entity) {
        if (!isRedirect(entity.getBody())) {
            return entity;
        }
        return mapToStatic(entity.getBody().replace(REDIRECT, ""));
    }

    private RequestHandler findMappedHandler(HttpRequest httpRequest) {
        RequestKey requestKey = new RequestKey(httpRequest.getMethod(), httpRequest.getPath());
        return map.get(requestKey);
    }

    private boolean isRedirect(String entityBody) {
        return entityBody.startsWith(REDIRECT);
    }

    private ResponseEntity mapToStatic(String path) {
        URL resource = findResource(path);
        if (resource == null) {
            return mapToNotFound();
        }
        return new ResponseEntity(HttpStatus.OK, loadFileAsString(resource), determineContentType(path));
    }

    private ContentType determineContentType(String path) {
        return ContentType.findWithExtension(path);
    }

    private static URL findResource(String path) {
        StringBuilder builder = new StringBuilder("static");
        if (!path.startsWith("/")) {
            builder.append("/");
        }
        return Thread.currentThread().getContextClassLoader().getResource(builder + path);
    }

    private static String loadFileAsString(URL resource) {
        try {
            File file = new File(resource.getFile());
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity mapToNotFound() {
        return new ResponseEntity(HttpStatus.NOT_FOUND, "", ContentType.TEXT_PLAIN);
    }

}
