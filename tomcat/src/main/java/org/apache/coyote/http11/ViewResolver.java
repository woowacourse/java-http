package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

class ViewResolver {

    private static final String GET_METHOD = "GET";
    private static final String DEFAULT_ROUTE = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final ResponseBinder responseBinder = new ResponseBinder();
    private final ContentTypeConverter contentTypeConverter = new ContentTypeConverter();

    String handle(HttpRequest request) throws IOException {
        if (GET_METHOD.equals(request.getHttpMethod())) {
            return handleGetRequest(request.getPath());
        }

        return responseBinder.buildNotFoundResponse();
    }

    private String handleGetRequest(String path) throws IOException {
        if (DEFAULT_ROUTE.equals(path)) {
            return responseBinder.buildSuccessfulResponse(DEFAULT_RESPONSE_BODY);
        }

        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            if (path.split("[.]").length == 0) {
                return handleNoFileExtensionRequest(path);
            }
            return responseBinder.buildNotFoundResponse();
        }

        return handleFileExtensionRequest(path, resource);
    }

    private String handleNoFileExtensionRequest(String path) throws IOException {
        path += ".html";
        URL staticResourceUrl = getClass().getClassLoader().getResource("static" + path);
        if (staticResourceUrl == null) {
            return responseBinder.buildNotFoundResponse();
        }

        return responseBinder.buildSuccessfulResponse(
                new String(Files.readAllBytes(new File(staticResourceUrl.getFile()).toPath())));
    }

    private String handleFileExtensionRequest(String path, URL resource) throws IOException {
        String staticResource = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String fileExtension = path.split("[.]")[1];
        String contentType = contentTypeConverter.mapToContentType(fileExtension);

        return responseBinder.buildSuccessfulResponse(contentType, staticResource);
    }
}
