package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResourceController extends AbstractController {
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_RESOURCE_PATH = "/login.html";

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException, URISyntaxException {
        String resourcePath = resolveResourcePath(request.path());

        byte[] responseBody = ROOT_RESPONSE_BODY.getBytes();

        if (!resourcePath.equals("/")) {
            String fileName = STATIC_RESOURCE_PREFIX + resourcePath;
            URL resource = getClass()
                    .getClassLoader()
                    .getResource(fileName);

            if (resource != null) {
                Path path = Paths.get(resource.toURI());
                responseBody = Files.readAllBytes(path);
            }
        }

        response.setStatus(HttpStatus.OK);
        response.addHeader(
                "Content-Type",
                contentTypeOf(request.extension())
        );
        response.setBody(responseBody);
    }

    private String resolveResourcePath(String requestPath) {
        if (requestPath.equals(LOGIN_PATH)) {
            return LOGIN_RESOURCE_PATH;
        } else if (requestPath.equals("/register")) {
            return "/register.html";
        }
        return requestPath;
    }

    private String contentTypeOf(String extension) {
        if (extension.equals("css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
