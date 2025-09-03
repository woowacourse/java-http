package com.techcourse.servlet;

public class CssServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/css/styles.css";
    }
}
