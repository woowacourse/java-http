package org.apache.catalina;

import org.apache.coyote.http11.response.Http11Response;

public interface Resolver {

    void resolve(String resourcePath, Http11Response response);
}
