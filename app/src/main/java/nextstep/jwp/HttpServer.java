package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import nextstep.jwp.constants.Headers;
import nextstep.jwp.constants.Http;
import nextstep.jwp.constants.StatusCode;
import nextstep.jwp.controller.FrontController;
import nextstep.jwp.exception.PageNotFoundException;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestHeader;
import nextstep.jwp.request.RequestLine;
import nextstep.jwp.response.ResponseEntity;

public class HttpServer {
    private final BufferedReader reader;
    private final RequestLine requestLine;
    private final RequestHeader headers;
    private final RequestBody body;

    public HttpServer(InputStream inputStream) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = new RequestLine(extractRequestLine());
        this.headers = new RequestHeader(extractHeaders());
        this.body = new RequestBody(extractRequestBody());
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
        if (headers.contains(Headers.CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.get(Headers.CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return Http.EMPTY_LINE;
    }

    public String getResponse() throws Exception {
        final FrontController frontController = new FrontController(this.body, this.requestLine);
        try {
            return frontController.response();
        } catch (PageNotFoundException e) {
            return ResponseEntity
                    .statusCode(StatusCode.NOT_FOUND)
                    .responseResource("/404.html")
                    .build();
        }
    }

}
