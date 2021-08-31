package nextstep.jwp.framework.response.details;

import java.util.HashMap;
import java.util.Map;

public class ResponseHttpHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";

    private final Map<String, String> responseHttpHeaderMap = new HashMap<>();

    public ResponseHttpHeader() {
    }

    public void appendResponseBodyInfo(String responseBody, String extension) {
        final int contentLength = responseBody.getBytes().length;
        responseHttpHeaderMap.put(CONTENT_LENGTH, String.valueOf(contentLength));

        final String contentType = FileExtensionHeaderValue.of(extension).getHeaderValue();
        responseHttpHeaderMap.put(CONTENT_TYPE, contentType);
    }

    public String generateResponse() {
        final StringBuilder response = new StringBuilder();

        for (String key : responseHttpHeaderMap.keySet()) {
            final String value = responseHttpHeaderMap.get(key);
            response.append(key).append(": ").append(value).append("\r\n");
        }

        return response.toString();
    }
}
