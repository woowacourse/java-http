package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.constants.Headers;
import nextstep.jwp.constants.Http;
import nextstep.jwp.controller.FrontController;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.RequestHeader;
import nextstep.jwp.http.RequestLine;

public class HttpServer {
    private final RequestLine requestLine;
    private final RequestHeader headers;
    private final RequestBody body;

    public HttpServer(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = new RequestLine(extractRequestLine(bufferedReader));
        this.headers = new RequestHeader(extractHeaders(bufferedReader));
        this.body = new RequestBody(extractRequestBody(bufferedReader));
    }

    private String extractHeaders(BufferedReader bufferedReader) throws IOException {
        final StringBuilder request = new StringBuilder();
        String line = null;
        while (!Http.EMPTY_LINE.equals(line)) {
            line = bufferedReader.readLine();

            if (line == null) {
                break;
            }

            request.append(line)
                    .append(Http.NEW_LINE);

        }
        return request.toString();
    }

    public String extractRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public String extractRequestBody(BufferedReader reader) throws IOException {
        if (headers.get(Headers.CONTENT_LENGTH).isPresent()) {
            int contentLength = Integer.parseInt(headers.get(Headers.CONTENT_LENGTH).get());
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return null;
    }

    public String getResponse() throws Exception {
        final FrontController frontController = new FrontController(this.body, this.requestLine);

        return frontController.response();
    }

}
