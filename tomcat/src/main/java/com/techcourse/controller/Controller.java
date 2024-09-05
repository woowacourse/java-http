package com.techcourse.controller;

import jakarta.servlet.http.HttpServlet;
import org.apache.coyote.http11.HttpRequest;

public interface Controller {

    void service(HttpRequest req);
}
