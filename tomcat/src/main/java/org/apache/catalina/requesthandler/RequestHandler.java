package org.apache.catalina.requesthandler;

import com.techcourse.exception.UnauthorizedException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javassist.NotFoundException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final RequestMappings requestMappings;

    public RequestHandler() {
        this.requestMappings = new RequestMappings();
    }

    public void handleRequest(HttpRequest request, HttpResponse response) {
        try {
            final var controller = requestMappings.getSupportController(request);
            controller.service(request, response);
        } catch (UnauthorizedException e) {
            response.setDefaultResponse(ResponseStatus.UNAUTHORIZED, ContentType.HTML, readUnauthorizedView());
        } catch (NotFoundException e) {
            response.setDefaultResponse(ResponseStatus.NOT_FOUND, ContentType.HTML, readNotFoundView());
        } catch (Exception e) {
            log.error("요청 처리 중 오류 발생", e);
            response.setDefaultResponse(ResponseStatus.INTERNAL_SERVER_ERROR, ContentType.HTML, readSeverErrorView());
        }
    }

    private byte[] readNotFoundView() {
        return readFile(Path.of("static", "404.html").toString());
    }

    private byte[] readUnauthorizedView() {
        return readFile(Path.of("static", "401.html").toString());
    }

    private byte[] readSeverErrorView() {
        return readFile(Path.of("static", "500.html").toString());
    }

    private byte[] readFile(String staticFilePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(staticFilePath)) {
            if (is == null) {
                throw new IllegalArgumentException("존재하지 않는 리소스입니다.: " + staticFilePath);
            }
            return is.readAllBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 불러오는데 실패했습니다.: " + staticFilePath, e);
        }
    }
}
