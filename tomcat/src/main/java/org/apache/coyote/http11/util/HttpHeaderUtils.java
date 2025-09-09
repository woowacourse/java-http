package org.apache.coyote.http11.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpHeaderUtils {

    private HttpHeaderUtils() {
    }

    /** Content-Type에서 charset= 파싱, 없으면 UTF-8 */
    public static Charset decideCharset(String contentType) {
        if (contentType != null) {
            for (String part : contentType.split(";")) {
                String p = part.trim().toLowerCase();
                if (p.startsWith("charset=")) {
                    String name = p.substring("charset=".length()).trim();
                    try { return Charset.forName(name); } catch (Exception ignored) {}
                }
            }
        }
        return StandardCharsets.UTF_8;
    }
}
