package nextstep.jwp.http.response;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHeader {

    private final Map<String, String> header;
    private ResponseLine line;

    public ResponseHeader() {
        this.header = new ConcurrentHashMap<>();
    }

    public void setContentType(String contentType) {
        header.put("Content-Type", contentType);
    }

    public void setContentLength(int contentLength) {
        header.put("Content-Length", String.valueOf(contentLength));
    }

    public void setLine(HttpStatus httpStatus) {
        this.line = new ResponseLine(httpStatus);
    }

    public Set<String> getKeySet() {
        return header.keySet();
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public ResponseLine getLine() {
        return line;
    }
}
