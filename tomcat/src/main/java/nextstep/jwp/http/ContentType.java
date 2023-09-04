package nextstep.jwp.http;

public enum ContentType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static String extractValueFromPath(String nativePath) {
        if (nativePath.endsWith(".css")) {
            return TEXT_CSS.value;
        }

        if (nativePath.endsWith(".js")) {
            return APPLICATION_JAVASCRIPT.value;
        }

        return TEXT_HTML.value;
    }

}
