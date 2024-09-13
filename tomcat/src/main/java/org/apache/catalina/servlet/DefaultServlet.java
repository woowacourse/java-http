package org.apache.catalina.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DefaultServlet {

    private static DefaultServlet instance;

    private DefaultServlet() {
    }

    public static DefaultServlet getInstance() {
        if (instance == null) {
            instance = new DefaultServlet();
        }
        return instance;
    }

    public void service(HttpRequest request, HttpResponse response) throws URISyntaxException {
        String resourceName = response.getResourceName();
        resourceName = getStaticResource(request, response, resourceName);
        String responseBody = getResource(resourceName);
        String resourceExtension = getExtension(resourceName);

        setSession(request, response);
        response.setResponseBody(responseBody);
        response.setContentType(resourceExtension);
    }

    private String getStaticResource(HttpRequest request, HttpResponse response, String resourceName) {
        if (resourceName == null) {
            resourceName = request.getPath();
            response.setResourceName(resourceName);
        }
        return resourceName;
    }

    private String getExtension(String resourceName) {
        if (resourceName == null) {
            return "html";
        }
        int extensionIndex = resourceName.indexOf('.') + 1;
        if (extensionIndex == 0) {
            return "html";
        }
        return resourceName.substring(extensionIndex);
    }

    private String getResource(String resourceName) throws URISyntaxException {
        URL resource = getClass().getClassLoader()
                .getResource("static" + resourceName);

        if (resource == null) {
            return "Hello world!";
        }
        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    private void setSession(HttpRequest request, HttpResponse response) {
        if (!request.hasCookieFrom("JSESSIONID")) {
            String sessionId = UUID.randomUUID().toString();
            response.setHttpCookie(HttpCookie.ofJSessionId(sessionId));
        }
    }
}
