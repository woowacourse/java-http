package com.techcourse.infrastructure;

import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public interface Presentation {
    HttpResponse view(HttpRequest request);

    boolean match(HttpRequest request);
}
