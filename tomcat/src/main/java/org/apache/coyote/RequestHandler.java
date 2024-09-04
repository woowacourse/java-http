package org.apache.coyote;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class RequestHandler {
    private static final Set<String> STATIC_RESOURCE_EXTENSIONS = Set.of("css", "js", "ico");
    private static final String STATIC_RESOURCE_ROOT_PATH = "static/";
    private static final String PATH_DELIMITER = "/";

    public String handle(final HttpRequest httpRequest) throws IOException {
        final String endPoint = httpRequest.uri().getPath();
        final String[] paths = endPoint.split(PATH_DELIMITER);

        if (paths.length == 0) {
            return processRootResponse();
        }

        final String resourceName = paths[paths.length - 1];
        if (resourceName.contains(".")) {
            return processStaticResponse(resourceName);
        }

        return processRootResponse();
    }

    private String processStaticResponse(final String resourceName) throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource(findResourcePath(resourceName));
        final Path resourcePath = Path.of(resourceURL.getPath());
        final String responseBody = Files.readString(resourcePath);
        final String mimeType = Files.probeContentType(resourcePath);

        return getResponse(mimeType, responseBody);
    }

    private String processRootResponse() {
        final String responseBody = "Hello world!";
        return getResponse("text/html", responseBody);
    }

    private String getResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType +";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String findResourcePath(final String resourcePath) {
        final String[] fileNames = resourcePath.split("\\.");
        final String extension = fileNames[1];

        if (STATIC_RESOURCE_EXTENSIONS.contains(extension)) {
            return STATIC_RESOURCE_ROOT_PATH.concat(extension).concat(PATH_DELIMITER).concat(resourcePath);
        }

        return STATIC_RESOURCE_ROOT_PATH.concat(resourcePath);
    }
}
