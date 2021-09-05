package nextstep.jwp.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {
    private static final Logger logger = LoggerFactory.getLogger(RequestLine.class);

    private Method method;
    private String resource;

    public RequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            return;
        }
        String[] requestLine = line.split(" ");
        method = Method.toMethod(requestLine[0]);
        resource = requestLine[1];
        logger.info("requestline parsing finished {}, {}.", method, resource);
    }

    public Method getMethod() {
        return method;
    }

    public boolean isGet(){
        return Method.GET == method;
    }

    public boolean isPost(){
        return Method.POST == method;
    }

    public String getResource() {
        return resource;
    }
}
