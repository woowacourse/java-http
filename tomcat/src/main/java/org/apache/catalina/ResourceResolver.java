package org.apache.catalina;

import java.net.URL;

public class ResourceResolver {

    private static final String PATTERN = ".*\\.(html|css|js|svg)$";

    public URL resolver(final String url) {
        String uri = url;
        if (!uri.matches(PATTERN)) {
            uri += ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        validateNullResource(resource);
        return resource;
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }

}
