package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.UnsupportedContentTypeException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestLine;

public enum ContentType {

    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    ALL("*/*");

    public static final String ACCEPT_HEADER = "Accept";
    private static final ContentType DEFAULT = HTML;
    private static final String FIELD_DELIMITER = ",";
    private static final String WEIGHT_DELIMITER = ";";
    private static final Map<String, ContentType> contentTypesByType;

    static {
        contentTypesByType = Arrays.stream(values())
            .collect(
                Collectors.toMap(ContentType::getType, contentType -> contentType));
    }

    private final String type;

    ContentType(final String type) {
        this.type = type;
    }

    public static ContentType findResponseContentTypeFromRequest(final HttpRequest httpRequest) {
        final Optional<String> acceptValues = httpRequest.getHeaderValues(ACCEPT_HEADER);

        ContentType acceptedContentType = ALL;
        if (acceptValues.isPresent()) {
            final List<String> acceptTypes = parseAcceptTypes(acceptValues.get());
            acceptedContentType = findContentTypeFromAcceptTypes(acceptTypes);
        }

        if (acceptedContentType != ALL) {
            return acceptedContentType;
        }
        return findContentTypeFromRequestPath(httpRequest.getRequestLine());
    }

    private static List<String> parseAcceptTypes(final String rawAcceptTypes) {
        return Arrays.stream(rawAcceptTypes.split(FIELD_DELIMITER))
            .map(ContentType::removeWeight)
            .collect(Collectors.toList());
    }

    private static String removeWeight(final String value) {
        final int weightIndex = value.indexOf(WEIGHT_DELIMITER);
        if (weightIndex == -1) {
            return value;
        }
        return value.substring(0, weightIndex);
    }

    private static ContentType findContentTypeFromAcceptTypes(final List<String> types) {
        return types.stream()
            .map(contentTypesByType::get)
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(UnsupportedContentTypeException::new);
    }

    private static ContentType findContentTypeFromRequestPath(final RequestLine requestLine) {
        return requestLine.getFileExtension()
            .flatMap(ContentType::findContentTypeByName)
            .orElse(DEFAULT);
    }

    private static Optional<ContentType> findContentTypeByName(final String name) {
        return Arrays.stream(values())
            .filter(contentType -> contentType.name().equalsIgnoreCase(name))
            .findAny();
    }

    public String getType() {
        return type;
    }
}
