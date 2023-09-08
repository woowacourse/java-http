package nextstep.jwp.controller;

import org.apache.http.ContentType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ViewResolver;
import org.apache.http.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@org.apache.http.annotation.Controller
public final class Controller {

    @RequestMapping(method = "GET", path = "/")
    public void hello(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, "html");
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.write("Hello world!");
    }

    @RequestMapping(method = "GET", path = "/index.html")
    public void index(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        httpResponse.write(Files.readString(viewResolver.getResourcePath()));
    }
}
