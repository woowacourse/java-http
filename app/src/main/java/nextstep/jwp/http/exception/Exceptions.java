package nextstep.jwp.http.exception;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import nextstep.jwp.http.Body;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.request_line.HttpPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.response_line.ResponseLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Exceptions {
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, NotFoundException.class),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, null);

    private static Logger log = LoggerFactory.getLogger(Exceptions.class);

    private final HttpStatus httpcode;
    private final Class<?> typeToken;

    Exceptions(HttpStatus httpcode, Class<?> typeToken) {
        this.httpcode = httpcode;
        this.typeToken = typeToken;
    }

    public static HttpResponse findResponseByException(Exception e) {
        Exceptions exception = Arrays.stream(values())
            .filter(value -> e.getClass().equals(value.typeToken))
            .findAny()
            .orElse(INTERNAL_SERVER_ERROR);

        return createResponse(exception);
    }

    private static HttpResponse createResponse(Exceptions e) {
        ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP1_1, e.httpcode);
        Headers headers = new Headers();
        Body body = new Body(getErrorPage(e), ContentType.TEXT_HTML.asString());

        headers.setBodyHeader(body);
        return new HttpResponse(responseLine, headers, body);
    }

    private static String getErrorPage(Exceptions e) {
        try {
            String resource = String.format("%d.html", e.httpcode.getCode());
            File file = new HttpPath(resource).toFile();

            return String.join(LINE_SEPARATOR, Files.readAllLines(file.toPath()));
        } catch (IOException ioException) {
            log.error("error page load fail!");
            return "Error!!" + e.httpcode.asString();
        }
    }
}