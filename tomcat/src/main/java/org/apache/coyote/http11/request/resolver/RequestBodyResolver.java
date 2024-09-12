package org.apache.coyote.http11.request.resolver;

import org.apache.coyote.http11.request.RequestBody;

public interface RequestBodyResolver <T> {

    T resolve(RequestBody requestBody);
}
