package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.Http;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.controller.MappingHandler;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.exception.PageNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestHeader;
import nextstep.jwp.request.RequestLine;
import nextstep.jwp.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer {
    private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

    private final BufferedReader reader;
    private final RequestHeader headers;
    private final MappingHandler mappingHandler;

    public HttpServer(InputStream inputStream) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        final RequestLine requestLine = new RequestLine(extractRequestLine());
        this.headers = new RequestHeader(extractHeaders());
        final RequestBody requestBody = new RequestBody(extractRequestBody());
        this.mappingHandler = new MappingHandler(requestLine, requestBody);
    }

    private String extractRequestLine() throws IOException {
        return reader.readLine();
    }

    private String extractHeaders() throws IOException {
        final StringBuilder headerLines = new StringBuilder();
        String header = null;
        while (!Http.EMPTY_LINE.equals(header)) {
            header = reader.readLine();
            if (Objects.isNull(header)) {
                break;
            }
            headerLines.append(header)
                    .append(Http.NEW_LINE);
        }
        return headerLines.toString();
    }

    private String extractRequestBody() throws IOException {
        if (headers.contains(Header.CONTENT_LENGTH.getKey())) {
            int contentLength = Integer.parseInt(headers.get(Header.CONTENT_LENGTH.getKey()));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return Http.EMPTY_LINE;
    }

    public String getResponse() throws IOException {
        try {
            return mappingHandler.response();
        } catch (InvocationTargetException e) {
            log.info(e.getMessage());
            return handleReflectionException(e);
        } catch (PageNotFoundException e) {
            log.info(e.getMessage());
            return ResponseEntity
                    .statusCode(StatusCode.NOT_FOUND)
                    .responseResource("/404.html")
                    .build();
        } catch (UnauthorizedException e) {
            log.info(e.getMessage());
            return ResponseEntity
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseResource("/401.html")
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .statusCode(StatusCode.INTERNAL_SERVER_ERROR)
                    .responseResource("/500.html")
                    .build();
        }
    }

    private String handleReflectionException(InvocationTargetException e) throws IOException {
        if (e.getTargetException() instanceof BadRequestException) {
            return ResponseEntity
                    .statusCode(StatusCode.BAD_REQUEST)
                    .responseResource("/400.html")
                    .build();
        }
        if (e.getTargetException() instanceof UnauthorizedException) {
            return ResponseEntity
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseResource("/401.html")
                    .build();
        }
        return ResponseEntity
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR)
                .responseResource("/500.html")
                .build();
    }
}
