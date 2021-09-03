package nextstep.jwp.http;

import nextstep.jwp.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {
    private static final Logger logger = LoggerFactory.getLogger(RequestLine.class);

    private String method;
    private String path;

    public RequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            return;
        }
        String[] requestLine = line.split(" ");
        method = requestLine[0];
        path = requestLine[1];
        logger.info("requestline parsing finished {}, {}.", method, path);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
