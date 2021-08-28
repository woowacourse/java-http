package nextstep.mockweb.result;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {

    private static final String HEADER_REGEX = ":";

    private Map<String, String> header = new HashMap<>();

    public void addHeader(String headerLine) {
        String[] splitedHeaders = headerLine.split(HEADER_REGEX);
        header.put(splitedHeaders[0], splitedHeaders[1].trim());
    }

    public String get(String key) {
        return header.get(key);
    }
}
