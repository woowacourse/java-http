package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {
    HttpResponse handle();
}
