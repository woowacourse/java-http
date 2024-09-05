package org.apache.coyote.handler;

import static org.apache.ResourceReader.readFile;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.Http11Response;

public class NotFoundHandler implements RequestHandler {

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return false;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            return Http11Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .appendHeader("Content-Type", "text/html;charset=utf-8 ")
                    .body(readFile("/404.html"))
                    .build();
        } catch (IOException e) {
            throw new UncheckedServletException(new NoSuchFieldException("파일을 찾을 수 없습니다."));
        }
    }
}
