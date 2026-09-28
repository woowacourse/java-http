package org.apache.coyote.http11.request.bodyParser;

import java.util.List;
import java.util.Map;

public class BodyParserResolver {

    private static final List<BodyParser> BODY_PARSERS = List.of(
            new FormBodyParser()
    );

    private BodyParserResolver() {
    }

    public static Map<String, String> parse(
            String contentType,
            String body
    ) {
        if (body == null || body.isBlank()) {
            return Map.of();
        }

        String resolvedContentType =
                contentType == null ? "" : contentType;

        return BODY_PARSERS.stream()
                .filter(parser -> parser.supports(resolvedContentType))
                .findFirst()
                .map(parser -> parser.parse(body))
                .orElseGet(Map::of);
    }
}
