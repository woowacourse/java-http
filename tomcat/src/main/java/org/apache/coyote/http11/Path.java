package org.apache.coyote.http11;

public class Path {

    private static final int EXCLUDE_SLASH_INDEX = 1;
    private static final int NOT_EXIST_QUERY_PARAMETER_CHARACTER = -1;

    private final String value;

    public Path(String uri) {
        int queryParameterIndex = uri.indexOf("?");
        if (queryParameterIndex != NOT_EXIST_QUERY_PARAMETER_CHARACTER) {
            this.value = uri.substring(queryParameterIndex);
            return;
        }
        this.value = uri;
    }

    public boolean isFileRequest() {
        return value.contains(".");
    }

    public boolean isLogin() {
        return value.equals("/login");
    }

    public String getFileName() {
        return value.substring(EXCLUDE_SLASH_INDEX);
    }

    public String extractContentType() {
        if (value.endsWith("css")) {
            return "css";
        }
        return "html";
    }
}
