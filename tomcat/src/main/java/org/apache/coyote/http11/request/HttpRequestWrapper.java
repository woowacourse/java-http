package org.apache.coyote.http11.request;


import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestWrapper {

    private static final String EMPTY_REQUEST = "요청이 비어있습니다.";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    List<String> lines;
    String method;
    String path;


    public HttpRequestWrapper(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new NoSuchElementException(EMPTY_REQUEST);
        }
        this.lines = lines;
        String[] firstLineSplit = lines.get(0).split(" ");
        this.method = firstLineSplit[0];
        this.path = firstLineSplit[1];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getLines() {
        String delimiter = "\n";
        return String.join(delimiter, lines);
    }
}
