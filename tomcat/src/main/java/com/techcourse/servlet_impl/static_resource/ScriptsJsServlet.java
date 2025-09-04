package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

@Deprecated
public class ScriptsJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/js/scripts.js";
    }
}
