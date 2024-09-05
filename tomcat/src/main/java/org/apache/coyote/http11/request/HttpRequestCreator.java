package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestCreator {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestCreator.class);

    private HttpRequestCreator() {
    }

    public static HttpRequest createHttpRequest(BufferedReader reader) throws IOException {
        RequestStartLine startLine = createStartLine(reader);
        return new HttpRequest(startLine);
    }

    private static RequestStartLine createStartLine(BufferedReader reader) throws IOException {
        String startLine = readLine(reader);
        return createStartLine(startLine);
    }

    private static RequestStartLine createStartLine(String startline) {
        String[] startLineComponents = startline.split(" ");
        if (startLineComponents.length != 3) {
            throw new UncheckedServletException("요청 시작 라인의 형식이 일치하지 않습니다.");
        }
        return new RequestStartLine(startLineComponents[0], startLineComponents[1], startLineComponents[2]);
    }

    private static String readLine(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        if (startLine == null) {
            throw new UncheckedServletException("HTTP 형식이 올바르지 않습니다");
        }
        return startLine;
    }
}
