package org.apache.catalina.resource;

import java.io.IOException;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.http11.data.MediaType;

public class ResourceHandler implements Controller {
    private static final ResourceHandler INSTANCE = new ResourceHandler();

    private final ResourceReader resourceReader = ResourceReader.getInstance();

    private ResourceHandler() {
    }

    public static ResourceHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getPath();
        if (path.equals("/")) {
            getRoot(httpResponse);
        } else if (path.endsWith("html")) {
            getHtml(path, httpResponse);
        } else if (path.endsWith("css")) {
            getCss(path, httpResponse);
        } else if (path.endsWith("js")) {
            getJs(path, httpResponse);
        }
    }

    public HttpResponse getRoot(HttpResponse httpResponse) {
        httpResponse.setContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody("Hello world!");
        return httpResponse;
    }

    public HttpResponse getHtml(String path, HttpResponse httpResponse) throws IOException {
        String responseBody = resourceReader.loadResourceAsString(path);
        httpResponse.setContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody(responseBody);
        return httpResponse;
    }

    public HttpResponse getCss(String path, HttpResponse httpResponse) throws IOException {
        String responseBody = resourceReader.loadResourceAsString(path);
        httpResponse.setContentType(new ContentType(MediaType.CSS, null))
                .setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody(responseBody);
        return httpResponse;
    }

    public HttpResponse getJs(String path, HttpResponse httpResponse) throws IOException {
        String responseBody = resourceReader.loadResourceAsString(path);
        httpResponse.setContentType(new ContentType(MediaType.JAVASCRIPT, null))
                .setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody(responseBody);
        return httpResponse;
    }
}
