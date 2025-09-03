package com.techcourse.servlet;

public class ScriptsJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/js/scripts.js";
    }
}
