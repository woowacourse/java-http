package org.apache.coyote.http11;

public class Path {

    private static final int EXCLUDE_SLASH_INDEX = 1;
    private static final int NOT_EXIST_QUERY_PARAMETER_CHARACTER = -1;

    private final String value;

    private Path(String uri) {
        this.value = uri;
    }

    public static Path of(String uri) {
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != NOT_EXIST_QUERY_PARAMETER_CHARACTER) {
            return new Path(uri.substring(0, queryParameterIndex));
        }
        return new Path(uri);
    }

    public boolean isFileRequest() {
        return value.contains(".");
    }

    public String getFileName() {
        return value.substring(EXCLUDE_SLASH_INDEX);
    }

    public String extractContentType() {
        ContentType contentType = ContentType.findContentType(value);
        return contentType.name().toLowerCase();
    }

    public String value() {
        return value;
    }
}
