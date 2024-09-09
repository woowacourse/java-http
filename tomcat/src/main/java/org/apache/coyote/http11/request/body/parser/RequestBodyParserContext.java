package org.apache.coyote.http11.request.body.parser;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.response.header.ContentType;

public class RequestBodyParserContext {

    private static final Map<ContentType, BodyParser> PARSER = new HashMap<>();

    static {
        PARSER.put(ContentType.APPLICATION_X_WWW_FORM_URLENCODED, new FormUrlEncodedParser());
    }

    public static Map<String, String> parse(String mediaType, String body) {
        ContentType contentType = ContentType.findByMediaType(mediaType);
        BodyParser bodyParser = PARSER.get(contentType);
        return bodyParser.parse(body);
    }
}
