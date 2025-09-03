package com.techcourse.servlet.static_resource;

import com.techcourse.servlet.StaticResourceServlet;

public class CssServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/css/styles.css";
    }
}
