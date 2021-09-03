package nextstep.jwp.http.request;

import nextstep.jwp.http.session.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private RequestHeaders headers;
    private RequestBody body;

    public HttpRequest(BufferedReader reader) {
        try {
            this.requestLine = new RequestLine(reader.readLine());
            this.headers = readHeaders(reader);
            this.body = readBody(reader, headers.getContentLength());
        } catch (IllegalStateException exception) {
            LOG.error("Exception invalid http request", exception);
        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        }
    }

    private RequestHeaders readHeaders(BufferedReader reader) throws IOException, IllegalStateException {
        RequestHeaders requestHeaders = new RequestHeaders();
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null) {
                throw new IllegalStateException("HTTP Header Line이 null일 수 없습니다.");
            }
            if ("".equals(line)) break;
            requestHeaders.put(line);
        }
        return requestHeaders;
    }

    private RequestBody readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new RequestBody(new String(buffer));
    }

    public String getParameter(String key) {
        return body.getParam(key);
    }

    public String getHeader(String key) {
        return headers.getHeader(key);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isGet() {
        return this.requestLine.isGet();
    }

    public boolean isPost() {
        return this.requestLine.isPost();
    }

    public boolean hasSessionId() {
        return this.headers.hasSessionId();
    }

    public HttpSession getSession() {
        return headers.getSession();
    }
}
