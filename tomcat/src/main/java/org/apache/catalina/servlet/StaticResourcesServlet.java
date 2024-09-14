package org.apache.catalina.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourcesServlet implements Servlet {

    public static final List<String> ALLOWED_EXTENSIONS = List.of("html", "css", "js", "png", "jpg", "ico", "svg");

    @Override
    public boolean canService(HttpRequest request) {
        return isAllowedExtension(request.getExtension()) && staticResourceExists(request);
    }

    private boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private boolean staticResourceExists(HttpRequest request) {
        String resourcePath = "static%s".formatted(request.getPathWithExtension());
        Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourcePath));

        return resource.isPresent();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String pathWithExtension = request.getPathWithExtension();

        response.setStaticResourceResponse(pathWithExtension);
        response.write();
    }
}
