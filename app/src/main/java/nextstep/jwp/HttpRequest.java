package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final InputStream inputStream;
    private String requestLine;
    private String[] splitRequestLine;

    public HttpRequest(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        init();
    }

    private void init() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        log.debug("line : {}", line);
        this.requestLine = line;
        this.splitRequestLine = requestLine.split(" ");
        while (!"".equals(line)) {
            if (line == null) return;
            line = bufferedReader.readLine();
            log.debug("line : {}", line);
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
}
