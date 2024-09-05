package com.techcourse.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;

public interface Handler {

    boolean support(HttpRequest request);

    View handle(HttpRequest request);
}
