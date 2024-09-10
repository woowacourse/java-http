package org.apache.coyote.http11.request.line;

import com.techcourse.exception.UncheckedServletException;
import java.util.Objects;

public class Uri {

    private static final String HOME = "/";

    private final String path;

    public Uri(String path) {
        this.path = path;
    }

    public boolean isStartsWith(Uri uri) {
        if (path.startsWith(uri.path)) {
            return true;
        }
        return false;
    }

    public String getQueryParameter(String name) {
        String queryString = getQueryString();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length != 2) {
                throw new UncheckedServletException("쿼리 파라미터 형식이 잘못되어 있습니다");
            }
            if (keyValue[0].equals(name)) {
                return keyValue[1];
            }
        }

        throw new UncheckedServletException(name + "에 해당하는 쿼리파라미터를 찾을 수 없습니다");
    }

    private String getQueryString() {
        int index = path.indexOf("?");
        return path.substring(index + 1);
    }

    public String getPath() {
        return path;
    }

    public boolean isHome() {
        return HOME.equals(path);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Uri uri = (Uri) o;
        return Objects.equals(path, uri.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
