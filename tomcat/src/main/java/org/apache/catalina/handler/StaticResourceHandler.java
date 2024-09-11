package org.apache.catalina.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;

public class StaticResourceHandler {

    public static final String RESOURCE_BASE_PATH = "static";

    private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {
    }

    public static StaticResourceHandler getInstance() {
        return INSTANCE;
    }

    public boolean canHandleRequest(HttpRequest request) {
        URL resource = findResource(request);
        return resource != null;
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String body = createResponseBody(request);
        Map<String, String> header = createResponseHeader(request, body);
        response.setResponse(HttpStatusCode.OK, new ResponseHeader(header), new ResponseBody(body));
    }

    private URL findResource(HttpRequest request) {
        String requestPath = request.getPath();
        return getClass().getClassLoader().getResource(RESOURCE_BASE_PATH + requestPath);
    }

    private String createResponseBody(HttpRequest request) throws IOException {
        URL resource = findResource(request);
        if (resource == null) {
            throw new IllegalArgumentException("요청 경로에 대한 자원이 존재하지 않습니다.");
        }

        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private static Map<String, String> createResponseHeader(HttpRequest request, String body) {
        Map<String, String> header = new HashMap<>();
        header.put(HttpHeaders.CONTENT_TYPE.getName(), request.getContentType());
        header.put(HttpHeaders.CONTENT_LENGTH.getName(), String.valueOf(body.length()));
        return header;
    }
}
