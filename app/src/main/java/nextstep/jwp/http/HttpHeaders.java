package nextstep.jwp.http;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHeaders {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHeaders.class);
    private static final String DELIMITER = ":";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void put(final String header) {
        LOGGER.debug("header : {}", header);

        int index = header.indexOf(DELIMITER);
        String headerKey = header.substring(0, index);
        String headerValue = header.substring(index + 2);

        headers.put(headerKey.trim(), headerValue.trim());
    }

    public String getHeaderDataByKey(final String key) {
        isValidateKey(key);
        return headers.get(key);
    }

    public String getHeaderByKey(final String key) {
        isValidateKey(key);
        return key + DELIMITER + " " + headers.get(key);
    }

    private void isValidateKey(final String key) {
        if (!headers.containsKey(key)) {
            LOGGER.error("없는 key의 헤더를 찾으려고 했습니다. key = {}", key);
            throw new IllegalArgumentException(String.format("없는 key의 헤더를 찾으려고 했습니다. key = {%s}", key));
        }
    }

    public String getAllHeaders() {
        StringBuilder headerSet = new StringBuilder();
        for (String key : headers.keySet()) {
            headerSet.append(key).append(DELIMITER + " ").append(headers.get(key)).append("\r\n");
        }
        return headerSet.toString();
    }
}
