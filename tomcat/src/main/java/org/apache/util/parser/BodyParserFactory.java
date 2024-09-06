package org.apache.util.parser;

import java.util.EnumMap;
import java.util.Map;
import org.apache.coyote.http11.message.common.ContentType;

public class BodyParserFactory {

    private static Map<ContentType, Parser> parsers = new EnumMap<>(ContentType.class);

    static {
        parsers.put(ContentType.FORM_DATA, new FormDataParser());
    }

    private BodyParserFactory() {
    }

    public static Parser getParser(ContentType contentType) {
        return parsers.get(contentType);
    }

}
