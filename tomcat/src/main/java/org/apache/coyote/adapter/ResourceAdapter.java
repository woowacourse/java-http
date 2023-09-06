package org.apache.coyote.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeader;

public class ResourceAdapter implements Adapter {

    private static final String DEFAULT_RESOURCE_PATH = "/static";
    private static final String NOT_FOUND_RESOURCE_PATH = "/404.html";
    private static final String HTML_TYPE = ".html";

    @Override
    public Response doHandle(Request request) {
        String path = request.getPath();
        try {
            PathStatus pathStatus = getResourcePath(path);
            ResponseBody responseBody = new ResponseBody(new String(Files.readAllBytes(pathStatus.getPath())));

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", request.getResourceTypes() + ";charset=utf-8");
            headers.put("Content-Length", responseBody.getLength());
            ResponseHeader responseHeader = new ResponseHeader(request.getProtocol(), pathStatus.getHttpStatus(), headers);
            return new Response(responseHeader, responseBody);

        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("파일 경로가 잘못되었습니다.");
        }
    }

    public PathStatus getResourcePath(String path) throws URISyntaxException {
        if (getResource(path) == null && getResource(path + HTML_TYPE) != null) {
            return new PathStatus(Path.of(getResource(path + HTML_TYPE).toURI()), HttpStatus.OK);
        }
        if (getResource(path) == null) {
            return new PathStatus(Path.of(getResource(NOT_FOUND_RESOURCE_PATH).toURI()), HttpStatus.NOT_FOUND);
        }
        return new PathStatus(Path.of(getResource(path).toURI()), HttpStatus.OK);
    }

    private URL getResource(String path) {
        return ResourceAdapter.class.getResource(DEFAULT_RESOURCE_PATH + path);
    }

    private class PathStatus {

        private final Path path;
        private final HttpStatus httpStatus;

        private PathStatus(Path path, HttpStatus httpStatus) {
            this.path = path;
            this.httpStatus = httpStatus;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public Path getPath() {
            return path;
        }
    }
}
