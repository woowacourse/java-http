package com.techcourse.servlet.static_resource;

import com.techcourse.servlet.StaticResourceServlet;

public class LoginPageServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/login.html";
    }
}
