package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class Handlers {

    private static final Pattern FILE_PATTERN = Pattern.compile("css|js|ico|html");

    private static final Map<String, Handler> handlers =
            Map.of(
                    "/", new RootHandler(),
                    "/file", new FileHandler(),
                    "/login", new LoginHandler()
            );


    public static ResponseEntity handle(HttpRequest request) throws IOException {
        String requestUri = request.getEndPoint();

        int lastDotIndex = requestUri.lastIndexOf('.');
        String extensionName = requestUri.substring(lastDotIndex + 1);
        if (FILE_PATTERN.matcher(extensionName).find()) {
            return handlers.get("/file").handle(request);
        }
        if (requestUri.contains("?")) {
            return handlers.get(requestUri).handle(request);
        }
        return handlers.get(requestUri).handle(request);
    }
}
