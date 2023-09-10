package org.apache.servlet.customservlet;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.servlet.ServletException;
import org.apache.servlet.SimpleHttpServlet;
import org.apache.servlet.SimpleWebServlet;

@SimpleWebServlet("/")
public class RootServlet extends SimpleHttpServlet {

    @Override
    public void doGet(
            HttpRequest request, HttpResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setBody("Hello world!".getBytes());
    }
}
