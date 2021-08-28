package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final BufferedReader bufferedReader;
    private RequestLine requestLine;
    private HttpHeaders headers;
    private Parameters parameters;

    public HttpRequest(InputStream inputStream) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        init();
    }

    private void init() throws IOException {
        String line = bufferedReader.readLine();
        log.debug("line : {}", line);
        this.requestLine = new RequestLine(line);
        Map<String, String> headers = new HashMap<>();
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            if (line == null) return;
            log.debug("line : {}", line);
            if (!"".equals(line)) {
                String[] splitHeader = line.split(": ");
                headers.put(splitHeader[0], splitHeader[1]);
            }
        }
        this.headers = new HttpHeaders(headers);
        this.parameters = new Parameters(getQueryString(), getRequestBody());
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getQueryString() {
        return requestLine.getQueryString();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getRequestBody() throws IOException {
        String length = getHeaders().get("Content-Length");
        if (length == null) {
            return null;
        }
        int contentLength = Integer.parseInt(length);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
