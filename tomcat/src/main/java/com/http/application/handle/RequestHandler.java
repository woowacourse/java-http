package com.http.application.handle;

import com.http.domain.HttpRequest;

public interface RequestHandler {

    void handle(HttpRequest httpRequest);
}
