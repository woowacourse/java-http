package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHeader {

    private static final String SPLIT_DELIMITER = ": ";

    private final Map<String, String> header;

    public RequestHeader() {
        this.header = new ConcurrentHashMap<>();
    }

    public void setHeader(String line) {
        String[] splitLine = line.split(SPLIT_DELIMITER);
        header.put(splitLine[0], splitLine[1]);
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
