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

    public ContentType(MediaType mediaType, String charSet) {
        this.mediaType = mediaType;
        this.charSet = charSet;
    }

    private String getCharSetFromContents(String[] contents) {
        if (contents.length == 2) {
            return contents[1];
        }
        return null;
    }

    public static ContentType from(String postfix) {
        MediaType mediaType = MediaType.findByPostfix(postfix)
                .orElseThrow(() -> new IllegalArgumentException(postfix + "로 끝나는 미디어 타입이 없습니다."));
        return new ContentType(mediaType, null);
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getString() {
        if (charSet == null) {
            return mediaType.getTypeName();
        }
        return mediaType.getTypeName() + DELIMITER + charSet;
    }
}
