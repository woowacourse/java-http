package nextstep.jwp.framework.util;

import java.util.Objects;

public class StringUtils {

    public static String requireNonBlank(String str) {
        if (!hasLength(str)) {
            throw new IllegalArgumentException("빈 문자열입니다.");
        }
        return str;
    }

    public static boolean hasLength(String str) {
        return Objects.nonNull(str) && !str.isBlank();
    }
}
