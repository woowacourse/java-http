package nextstep.jwp.controller;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@org.apache.coyote.http11.annotation.Controller
public final class Controller {

    @RequestMapping(method = "GET", path = "/")
    public void hello(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
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
