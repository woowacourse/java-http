package org.apache.coyote.http11;

public class ContentType {
    private static final String DELIMITER = ";";

    private final MediaType mediaType;
    private final String charSet;

    public ContentType(String rawContentType) {
        String[] contents = rawContentType.split(DELIMITER);
        this.mediaType = MediaType.from(contents[0])
                .orElseThrow(() -> new IllegalArgumentException("올바른 HTTP Content-Type이 아닙니다."));
        this.charSet = getCharSetFromContents(contents);
    }

    private String getCharSetFromContents(String[] contents) {
        if (contents.length == 2) {
            return contents[1];
        }
        return null;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getString() {
        return mediaType.getTypeName() + DELIMITER + charSet;
    }
}
