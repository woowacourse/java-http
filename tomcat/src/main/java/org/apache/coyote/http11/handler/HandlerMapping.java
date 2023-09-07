package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.ResponseLine;

public class HandlerMapping {

    private static final List<HttpHandler> HTTP_HANDLERS = List.of(
            new ResourceHandler(),
            new DefaultHandler(),
            new IndexHandler(),
            new LoginHandler(),
            new RegisterHandler()
    );

    public HttpResponse extractHttpResponse(HttpRequest request) {
        ResponseEntity responseEntity = handle(request);
        File viewFile = ViewResolver.findViewFile(responseEntity.getPath());

        return new HttpResponse(
                new ResponseLine(responseEntity.getHttpStatus()),
                ResponseHeaders.from(responseEntity, MimeType.from(viewFile)),
                new ResponseBody(readString(viewFile))
        );
    }

    private ResponseEntity handle(final HttpRequest request) {
        try {
            return HTTP_HANDLERS.stream()
                    .filter(httpHandler -> httpHandler.canHandle(request))
                    .findFirst()
                    .orElseGet(NotFoundHandler::new)
                    .handle(request);
        } catch (RuntimeException e) {
            return ResponseEntity.of(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
        }
    }

    private String readString(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            return "";
        }
    }

}
