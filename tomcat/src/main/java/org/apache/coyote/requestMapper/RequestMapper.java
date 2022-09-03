package org.apache.coyote.requestMapper;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface RequestMapper {
    HttpResponse mapping(HttpRequest httpRequest);
}
