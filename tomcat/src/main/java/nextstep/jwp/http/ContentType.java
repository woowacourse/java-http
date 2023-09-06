package nextstep.jwp.http;

public enum ContentType {
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_CSS("text/css;charset=utf-8"),
    APPLICATION_JAVASCRIPT("application/javascript;charset=utf-8"),
    TEXT_PLAIN("text/plain;charset=utf-8");

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

        if (nativePath.endsWith(".html")) {
            return TEXT_HTML.value;
        }

        return TEXT_PLAIN.value;
    }

}
