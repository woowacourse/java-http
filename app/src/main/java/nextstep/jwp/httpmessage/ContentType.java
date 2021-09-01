package nextstep.jwp.httpmessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ContentType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript; charset=UTF-8"),
    X_WWW_FORM("XWWWFORM", "application/x-www-form-urlencoded"),
    IMAGE(".svg", "image/svg+xml"),
    ICON(".cio", "image/x-icon");

    public static Map<String, ContentType> contentTypes = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach(it -> contentTypes.put(it.getSuffix(), it));
    }

    private final String suffix;
    private final String value;

    ContentType(String suffix, String value) {
        this.suffix = suffix;
        this.value = value;
    }

    public static boolean containValueByUrl(String target) {
        for (String key : contentTypes.keySet()) {
            if (target.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public static String getValueByUri(String target) {
        for (Map.Entry<String, ContentType> entry : contentTypes.entrySet()) {
            if (target.contains(entry.getKey())) {
                return entry.getValue().value;
            }
        }
        throw new IllegalStateException("찾을 수 없는 확장자 입니다.");
    }

    public static String getSuffixByUri(String target) {
        for (Map.Entry<String, ContentType> entry : contentTypes.entrySet()) {
            if (target.contains(entry.getKey())) {
                return entry.getValue().suffix;
            }
        }
        throw new IllegalStateException("찾을 수 없는 확장자 입니다.");
    }

    public static ContentType getContentTypeByUri(String target) {
        return contentTypes.get(getSuffixByUri(target));
    }

    public String getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }
}
