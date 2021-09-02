package nextstep.jwp.framework.http.response.details;

import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.framework.http.common.Constants.*;

public class ResponseHttpHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> responseHttpHeaderMap;

    public ResponseHttpHeader() {
        responseHttpHeaderMap = new HashMap<>();
    }

    public void appendResponseBodyInfo(final String responseBody, final String extension) {
        final int contentLength = responseBody.getBytes().length;
        responseHttpHeaderMap.put(CONTENT_LENGTH, String.valueOf(contentLength));

        final String contentType = FileExtensionHeaderValue.of(extension).getHeaderValue();
        responseHttpHeaderMap.put(CONTENT_TYPE, contentType);
    }

    public void appendRedirectInfo(final String location) {
        responseHttpHeaderMap.put(LOCATION, location);
    }

    public void appendSessionInfo(final String sessionId) {
        responseHttpHeaderMap.put(SET_COOKIE, JSESSIONID + "=" + sessionId);
    }

    public String generateResponse() {
        final StringBuilder response = new StringBuilder();

        for (Map.Entry<String, String> responseHttpHeader : responseHttpHeaderMap.entrySet()) {
            final String headerKey = responseHttpHeader.getKey();
            final String headerValue = responseHttpHeader.getValue();
            response.append(headerKey).append(HTTP_HEADER_SEPARATOR).append(LINE_SEPARATOR)
                    .append(headerValue).append(NEWLINE);
        }

        return response.toString();
    }
}
