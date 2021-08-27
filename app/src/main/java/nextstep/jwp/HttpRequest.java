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
    private String requestLine;
    private String[] splitRequestLine;
    private Map<String, String> headers;
    private Map<String, String> parameters;

    public HttpRequest(InputStream inputStream) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));;
        init();
    }

    private void init() throws IOException {
        String line = bufferedReader.readLine();
        log.debug("line : {}", line);
        this.requestLine = line;
        this.splitRequestLine = requestLine.split(" ");
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            if (line == null) return;
            log.debug("line : {}", line);
            if (!"".equals(line)) {
                String[] splitHeader = line.split(": ");
                headers.put(splitHeader[0], splitHeader[1]);
            }
        }
        parseParameters();
    }

    private void parseParameters() throws IOException {
        parseQueryString(getQueryString());
        parseQueryString(getRequestBody());
    }

    private void parseQueryString(String queryString) {
        if (queryString != null && !"".equals(queryString)) {
            String[] split = queryString.split("&");
            for (String data : split) {
                String[] splitData = data.split("=");
                parameters.put(splitData[0], splitData[1]);
            }
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getMethod() {
        return splitRequestLine[0];
    }

    public String getRequestURI() {
        return splitRequestLine[1];
    }

    public String getQueryString() {
        String requestURI = getRequestURI();
        int index = requestURI.indexOf("?");
        if (index != -1) {
            return requestURI.substring(index + 1);
        }
        return null;
    }

    public String getPath() {
        String requestURI = getRequestURI();
        int index = requestURI.indexOf("?");
        if (index != -1) {
            return requestURI.substring(0, index);
        }
        return requestURI;
    }

    public Map<String, String> getHeaders() {
        return headers;
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
