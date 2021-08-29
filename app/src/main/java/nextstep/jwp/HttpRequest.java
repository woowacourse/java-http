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

    public HttpRequest(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            this.requestLine = new RequestLine(line);
            initHeaders();
            this.parameters = new Parameters(getQueryString(), getRequestBody());
        } catch (Exception e) {
            log.error("Exception stream", e);
        }
    }

    private void initHeaders() throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !"".equals(line)) {
            String[] splitHeader = line.split(": ");
            headers.put(splitHeader[0], splitHeader[1]);
        }
        this.headers = new HttpHeaders(headers);
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

    private String getRequestBody() throws IOException {
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
