package com.http.application.servlet;

import com.http.domain.HttpRequest;

public interface RequestServlet {

    void handle(HttpRequest httpRequest);
}
