package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;

public interface Mapper {

    Controller getController(final HttpRequest request);
}
