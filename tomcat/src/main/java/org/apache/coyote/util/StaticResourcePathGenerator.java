package org.apache.coyote.util;

public class StaticResourcePathGenerator {

    public static String generate(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        if ("/".equals(path)) {
            return "static/hello.html";
        }
        path = path.replaceAll("/{2,}", "/");
        if (path.contains("..")) {
            return null;
        }
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path += "index.html";
        }
        String last = path.substring(path.lastIndexOf('/') + 1);
        if (!last.contains(".")) {
            path += ".html";
        }
        return "static" + path;
    }
}
