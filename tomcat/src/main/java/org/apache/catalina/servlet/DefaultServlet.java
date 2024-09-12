package org.apache.catalina.servlet;

import static org.apache.catalina.webresources.FileResource.NOT_FOUND_RESOURCE_URI;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.webresources.WebResource;

public class DefaultServlet extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        WebResource webResource = StandardRoot.getResource(request.getURI());
        if (webResource.exists()) {
            response.writeStaticResource(request.getURI());
            return;
        }
        response.sendRedirect(NOT_FOUND_RESOURCE_URI);
    }
}
