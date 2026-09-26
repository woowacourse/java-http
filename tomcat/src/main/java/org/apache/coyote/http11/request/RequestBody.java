package org.apache.coyote.http11.request;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class RequestBody {
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final char PARAMETER_DELIMITER = ';';
    private static final char SP = ' ';
    private static final char HTAB = '\t';

    private static final RequestBody EMPTY = new RequestBody(new byte[0], QueryParameters.empty());

    private final byte[] content;
    private final QueryParameters formParameters;

    private RequestBody(final byte[] content, final QueryParameters formParameters) {
        this.content = content;
        this.formParameters = formParameters;
    }

    public static RequestBody empty() {
        return EMPTY;
    }

    public static RequestBody of(final byte[] content, final Optional<String> contentType) {
        if (content.length == 0) {
            return EMPTY;
        }
        final byte[] copy = content.clone();
        if (!contentType.map(RequestBody::isFormUrlEncoded).orElse(false)) {
            return new RequestBody(copy, QueryParameters.empty());
        }
        // form 데이터는 percent-encoding된 ASCII. 바이트를 변형 없이 옮기고 UTF-8 해석은 PercentDecoder에 맡긴다
        final String raw = new String(copy, StandardCharsets.ISO_8859_1);
        return new RequestBody(copy, QueryParameters.from(raw));
    }


    // 미디어 타입은 대소문자를 구분하지 않고, ';' 뒤의 파라미터(charset 등)는 무시한다 (RFC 9110 8.3.1)
    private static boolean isFormUrlEncoded(final String contentType) {
        final int delimiterIndex = contentType.indexOf(PARAMETER_DELIMITER);
        final String mediaType = delimiterIndex == -1
                ? contentType
                : contentType.substring(0, delimiterIndex);
        return trimTrailingOws(mediaType).equalsIgnoreCase(FORM_URLENCODED);
    }

    private static String trimTrailingOws(final String value) {
        int end = value.length();
        while (end > 0 && (value.charAt(end - 1) == SP || value.charAt(end - 1) == HTAB)) {
            end--;
        }
        return value.substring(0, end);
    }

    public boolean hasParameters() {
        return !formParameters.isEmpty();
    }

    public Optional<String> getParameter(final String name) {
        return formParameters.get(name);
    }
}
