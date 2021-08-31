package nextstep.jwp.ui.request;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.ui.RequestHandler;
import nextstep.jwp.ui.common.HttpHeaders;
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
    private static final String FORM_TYPE = "application/x-www-form-urlencoded";

    private final BufferedReader bufferedReader;
    private RequestLine requestLine;
    private HttpHeaders headers;
    private Parameters parameters;

    public HttpRequest(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        readLines();
    }

    private void readLines() {
        try {
            String line = bufferedReader.readLine();
            if (line == null) return;
            this.requestLine = new RequestLine(line);
            this.headers = parseHeaders();
            this.parameters = parseParameters();
        } catch (IOException e) {
            log.error("http request read lines exception", e);
            throw new BadRequestException();
        }
    }

    private HttpHeaders parseHeaders() throws IOException {
        String line;
        Map<String, String> headers = new HashMap<>();
        while ((line = bufferedReader.readLine()) != null && !"".equals(line)) {
            String[] splitHeader = line.split(": ");
            headers.put(splitHeader[0].trim(), splitHeader[1].trim());
        }
        return new HttpHeaders(headers);
    }

    private Parameters parseParameters() throws IOException {
        Parameters parameters = new Parameters();
        parameters.addParameters(getQueryString());
        String length = getHeaders().get("Content-Length");
        if (!FORM_TYPE.equals(getHeaders().get("Content-Type")) || length == null) {
            return parameters;
        }
        int contentLength = Integer.parseInt(length);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        parameters.addParameters(new String(buffer));
        return parameters;
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

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
