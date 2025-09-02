package com.techcourse.servlet;

public class CssServlet extends StaticResourceServlet {

    @Override
    protected String uri() {
        return "/css/styles.css";
    }

    @Override
    protected String resourcePath() {
        return "static/css/styles.css";
    }

    @Override
    protected StaticResourceType type() {
        return StaticResourceType.CSS;
    }
}
