package org.apache.coyote.http11.header;

import java.net.URL;
import org.apache.coyote.SubstringGenerator;

public class RequestTarget {

    private final String value;

    public RequestTarget(String value) {
        this.value = value;
    }

    public boolean startsWith(String startsWith) {
        return value.startsWith(startsWith);
    }

    public boolean isEqualTo(String target) {
        return value.equals(target);
    }

    public String getTargetExtension() {
        if (value.contains(".")) {
            return SubstringGenerator.splitByLast(".", value).getLast();
        }
        return "html";
    }

    public URL getUrl() {
        String path = value;
        if (!path.contains(".")) {
            path = path + ".html";
        }
        return getClass().getClassLoader().getResource("static" + path);
    }

    public boolean isBlank() {
        return value.isBlank() || value.equals("/");
    }
}
