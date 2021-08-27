package nextstep.jwp.http.request;

import com.google.common.base.Strings;
import nextstep.jwp.exception.InvalidRequestHeadersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private static final Logger log = LoggerFactory.getLogger(RequestHeaders.class);

    private Map<String, String> headers = new HashMap<>();

    public RequestHeaders(BufferedReader reader) {
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    throw new InvalidRequestHeadersException("line이 null일 수 없습니다.");
                }
                if ("".equals(line)) break;
                String[] tokens = line.split(": ");
                headers.put(tokens[0], tokens[1]);
            }
        } catch (Exception exception) {
            log.error("Exception buffered reader read headers", exception);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (Strings.isNullOrEmpty(contentLength)) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }
}
