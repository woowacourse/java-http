package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;

public interface RequestMapper {

    Controller getController(HttpRequest request);
}
