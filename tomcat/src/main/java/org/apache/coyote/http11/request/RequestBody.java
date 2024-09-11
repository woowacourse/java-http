package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.component.MediaType;
import org.apache.coyote.http11.request.parser.PlainTextParser;
import org.apache.coyote.http11.request.parser.RequestBodyParser;
import org.apache.coyote.http11.request.parser.WebFormUrlParser;

public class RequestBody {

    private static final Map<MediaType, RequestBodyParser> PARSERS = new EnumMap<>(MediaType.class);

    static {
        PARSERS.put(MediaType.APPLICATION_FORM_URLENCODED, new WebFormUrlParser());
        PARSERS.put(MediaType.TEXT_PLAIN, new PlainTextParser());
    }

    private final Map<String, String> parameters;

    public static RequestBody from(String body, String value) {
        if (StringUtils.isBlank(body)) {
            return new RequestBody(Collections.emptyMap());
        }
        MediaType mediaType = MediaType.from(value);
        return new RequestBody(PARSERS.get(mediaType).parseParameters(body));
    }

    public RequestBody(Map<String, String> body) {
        this.parameters = body;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
