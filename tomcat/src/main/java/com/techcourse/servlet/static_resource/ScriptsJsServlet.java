package com.techcourse.servlet.static_resource;

import com.techcourse.servlet.StaticResourceServlet;

public class ScriptsJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/js/scripts.js";
    }
}
