package org.apache.servlet.customservlet;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseUtil;
import org.apache.servlet.ServletException;
import org.apache.servlet.SimpleHttpServlet;

public class BadRequestServlet extends SimpleHttpServlet {

    @Override
    public void doGet(HttpRequest request, HttpResponse response)
            throws ServletException, IOException {
        ResponseUtil.setBadRequest(response);
    }
}
