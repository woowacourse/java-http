package com.techcourse.view;

import com.techcourse.exception.ApplicationException;
import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class ViewResolver {

    private static final String DEFAULT_ROUTE = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    public static final String RESOURCE_DIRECTORY = "static";

    public HttpResponse resolve(HttpRequest request, HttpResponse response) throws IOException, ApplicationException {
        String path = request.getPath();
        if (DEFAULT_ROUTE.equals(path)) {
            response.setStatusCode(HttpStatusCode.OK);
            response.setBody(DEFAULT_RESPONSE_BODY);
            response.setContentType("text/html");
            response.setContentLength();
            return response;
        }

        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        if (resource == null) {
            path += ".html";
        }

        return getStaticResourceResponse(response, path);
    }

    private HttpResponse getStaticResourceResponse(
            HttpResponse response, String path) throws IOException, ApplicationException {
        URL staticResourceUrl = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);

        if (staticResourceUrl != null) {
            File file = new File(staticResourceUrl.getFile());
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mimeType = fileNameMap.getContentTypeFor(file.getName());

            response.setStatusCode(HttpStatusCode.OK);
            response.setBody(new String(Files.readAllBytes(file.toPath())));
            response.setContentType(mimeType);
            response.setContentLength();
            return response;
        }

        return null;
    }
}
