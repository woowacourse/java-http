package org.apache.coyote;

import java.io.IOException;

public interface HttpResponse {

    void sendRedirect(String url);

    void addStaticBody(String name) throws IOException;

    void addCookie(String key, String value);

    void addContentType(String accept);

    void addBody(String body);
}
