package org.apache.mvc;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.util.FileUtil;
import org.apache.util.UrlUtil;

public class ControllerMapper {

    private static final String REDIRECT = "redirect:";

    private final Map<RequestKey, RequestHandler> map;

    private ControllerMapper(Map<RequestKey, RequestHandler> map) {
        this.map = map;
    }

    public static ControllerMapper from(List<Controller> controllers) {
        return new ControllerMapper(ControllerParser.parse(controllers));
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
        try {
            String filePath = UrlUtil.joinUrl("static", path);
            return new ResponseEntity(HttpStatus.OK, FileUtil.loadFile(filePath), determineContentType(path));
        } catch (IllegalArgumentException e) {
            return mapToNotFound();
        }
    }

    private ContentType determineContentType(String path) {
        return ContentType.findWithExtension(path);
    }

    private ResponseEntity mapToNotFound() {
        return new ResponseEntity(HttpStatus.NOT_FOUND, "", ContentType.TEXT_PLAIN);
    }

}
