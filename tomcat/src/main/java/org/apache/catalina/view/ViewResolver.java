package org.apache.catalina.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequestHeader;

public class ViewResolver {

    private static final String DEFAULT_ROUTE = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final ResponseBuilder responseBuilder = new ResponseBuilder();
    private final ContentTypeConverter contentTypeConverter = new ContentTypeConverter();

    public String resolve(String view) throws IOException {
        return handleGetRequest(view);
    }

    public String resolve(HttpRequestHeader request) throws IOException {
        if (HttpMethod.GET.equals(request.getHttpMethod())
                && (request.getPath().startsWith("/login") || request.getPath().startsWith("/register"))
                && (request.getCookies() != null && request.getCookies().get("JSESSIONID") != null)) {
            return responseBuilder.buildRedirectResponse("/index.html");
        }

        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            return handleGetRequest(request.getPath());
        }

        return responseBuilder.buildNotFoundResponse();
    }

    private String handleGetRequest(String path) throws IOException {
        if (DEFAULT_ROUTE.equals(path)) {
            return responseBuilder.buildSuccessfulResponse(DEFAULT_RESPONSE_BODY);
        }

        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            if (path.split("[.]").length == 1) {
                return handleNoFileExtensionRequest(path);
            }
            return responseBuilder.buildNotFoundResponse();
        }

        return handleFileExtensionRequest(path, resource);
    }

    private String handleNoFileExtensionRequest(String path) throws IOException {
        path += ".html";
        URL staticResourceUrl = getClass().getClassLoader().getResource("static" + path);
        if (staticResourceUrl == null) {
            return responseBuilder.buildNotFoundResponse();
        }

        return responseBuilder.buildSuccessfulResponse(
                new String(Files.readAllBytes(new File(staticResourceUrl.getFile()).toPath())));
    }

    private String handleFileExtensionRequest(String path, URL resource) throws IOException {
        String staticResource = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String fileExtension = path.split("[.]")[1];
        String contentType = contentTypeConverter.mapToContentType(fileExtension);

        return responseBuilder.buildSuccessfulResponse(contentType, staticResource);
    }
}
