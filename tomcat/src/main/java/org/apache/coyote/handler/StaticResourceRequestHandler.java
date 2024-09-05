package org.apache.coyote.handler;

import static org.apache.ResourceReader.canRead;
import static org.apache.ResourceReader.readFile;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.Http11Response;

public class StaticResourceRequestHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return canRead(httpRequest.getRequestURI()) && Http11Method.GET.equals(httpRequest.getMethod());
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            return Http11Response.builder()
                    .status(HttpStatus.OK)
                    .appendHeader("Content-Type", getContentType(httpRequest))
                    .body(readFile(httpRequest.getRequestURI()))
                    .build();
        } catch (IOException e) {
            throw new UncheckedServletException(new NoSuchFieldException("파일을 찾을 수 없습니다."));
        }
    }

    private String getContentType(HttpRequest httpRequest) {
        if (httpRequest.getHeader("Accept") == null) {
            int index = httpRequest.getRequestURI().indexOf(".");
            return "text/" + httpRequest.getRequestURI().substring(index + 1) + ";charset=utf-8 ";
        }
        return httpRequest.getHeader("Accept").split(",")[0];
    }
}
