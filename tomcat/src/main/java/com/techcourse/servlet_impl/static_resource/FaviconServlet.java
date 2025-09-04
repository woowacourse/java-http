package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

public class FaviconServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/favicon.ico";
    }
}
