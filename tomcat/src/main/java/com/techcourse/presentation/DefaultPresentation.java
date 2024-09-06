package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import http.HttpMethod;
import http.requestheader.HttpStatusCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class DefaultPresentation implements Presentation {

    private static final String STATIC_RESOURCE_ROOT = "/static";

    @Override
    public HttpResponse view(HttpRequest request) {
        String responseBody = findStaticResource(request.getPath());
        return new HttpResponse(
                HttpStatusCode.OK,
                request.getMediaType(),
                responseBody
        );
    }

    private String findStaticResource(String path) {
        Path staticResourcePath = getStaticResourcePath(path);
        try {
            return Files.readString(staticResourcePath);
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽을 수 없어요!");
        }
    }

    private Path getStaticResourcePath(String path) {
        try {
            URL staticResourceUrl = getStaticResourceUrl(path);
            return Paths.get(staticResourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("파일 경로를 찾을 수 없네요..");
        }
    }

    private URL getStaticResourceUrl(String path) {
        URL resource = getClass().getResource(STATIC_RESOURCE_ROOT + path);
        if (resource == null) {
            resource = getClass().getResource(STATIC_RESOURCE_ROOT + path + ".html");
        }
        return resource;
    }

    @Override
    public boolean match(HttpRequest request) {
        return HttpMethod.GET == request.getHttpMethod() && request.getQueryParam().isEmpty();
    }
}
