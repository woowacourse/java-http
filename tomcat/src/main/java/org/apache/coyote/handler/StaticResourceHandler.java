package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.Response;
import org.apache.coyote.common.StatusCode;

public class StaticResourceHandler implements Handler {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {
    }

    public static StaticResourceHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public Response handle(Request request) {
        return handle(request, StatusCode.OK);
    }

    public Response handle(Request request, StatusCode statusCode) {
        File responseBody = getStaticResource(request.getUri());
        try {
            return makeResponse(responseBody, statusCode);
        } catch (IOException e) {
            return new Response(StatusCode.INTERNAL_SERVER_ERROR, Map.of(), null);
        }
    }

    private File getStaticResource(String location) {
        if (location.equals("/")) {
            location = "/hello.html";
        }
        if (!location.contains(".")) {
            location += ".html";
        }
        File file;
        try {
            file = new File(getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + location).getFile());
        } catch (NullPointerException e) {
            file = new File(getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + "/404.html").getFile());
        }
        return file;
    }

    private Response makeResponse(File resource, StatusCode statusCode) throws IOException {
        byte[] responseBody = Files.readAllBytes(resource.toPath());
        return new Response(statusCode,
                            Map.of("Content-Type", getContentType(resource),
                                   "Content-Length", getResponseLength(responseBody)),
                            new String(responseBody));
    }

    private String getContentType(File resource) {
        return ContentType.of(resource).getMimeType();
    }

    private String getResponseLength(byte[] responseBody) {
        return String.valueOf(responseBody.length);
    }
}
