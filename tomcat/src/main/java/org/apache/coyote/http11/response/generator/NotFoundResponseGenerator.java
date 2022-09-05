package org.apache.coyote.http11.response.generator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundResponseGenerator implements ResponseGenerator {

    private static final String resourceName = "static/404.html";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return false;
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        URL url = getClass().getClassLoader()
                .getResource(resourceName);
        Path path = new File(url.getFile()).toPath();
        String responseBody = Files.readString(path);
        return HttpResponse.notFound(responseBody);
    }
}
