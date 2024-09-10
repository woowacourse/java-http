package org.apache.coyote.http11.component.common.body;

import java.util.function.Function;
import java.util.stream.Stream;

public enum BodyMapper {

    EMPTY("", TextTypeBody::new),
    TEXT_PLAIN("text/plain", TextTypeBody::new),
    TEXT_HTML("text/html", TextTypeBody::new),
    TEXT_CSS("text/css", TextTypeBody::new),
    TEXT_JS("text/javascript", TextTypeBody::new),
    FORM_URL_ENCODED("application/x-www-form-urlencoded", FormUrlEncodeBody::new);

    private final String mimeType;
    private final Function<String, Body> mapping;

    BodyMapper(final String mimeType, final Function<String, Body> mapping) {
        this.mimeType = mimeType;
        this.mapping = mapping;
    }

    public static Function<String, Body> getMapping(final String plaintext) {
        final var bodyMapper = Stream.of(values())
                .filter(value -> plaintext.equals(value.mimeType))
                .findAny()
                .orElseGet(() -> EMPTY);

        return bodyMapper.mapping;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Function<String, Body> getMapping() {
        return mapping;
    }
}
