package nextstep.jwp.http.common;

public enum ContentType {
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_CSS("text/css;charset=utf-8"),
    APPLICATION_JAVASCRIPT("application/javascript;charset=utf-8");

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
