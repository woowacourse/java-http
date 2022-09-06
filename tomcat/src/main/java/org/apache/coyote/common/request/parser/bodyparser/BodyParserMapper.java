package org.apache.coyote.common.request.parser.bodyparser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.common.MediaType;

public class BodyParserMapper {

    private static final Map<MediaType, Function<String, Map<String, String>>> cache;

    static {
        cache = new HashMap<>();
        cache.put(MediaType.MULTIPART_FORM_DATA, new MultipartFormDataBodyParser());
    }

    public static Function<String, Map<String, String>> of(final MediaType mediaType) {
        return cache.getOrDefault(mediaType, new PlainTextBodyParser());
    }
}
