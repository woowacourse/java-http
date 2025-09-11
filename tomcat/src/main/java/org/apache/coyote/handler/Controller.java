package org.apache.coyote.handler;

import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.dto.HttpResponse;

public interface Controller {
        void service(HttpRequest request, HttpResponse response) throws Exception;
}
