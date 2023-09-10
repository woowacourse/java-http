package org.apache.servlet.customservlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseUtil;
import org.apache.servlet.ServletException;
import org.apache.servlet.SimpleHttpServlet;
import org.apache.servlet.SimpleWebServlet;

// file uri regex
@SimpleWebServlet("^\\/(?:[a-zA-Z0-9_-]+\\/)*[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$")
public class GeneralStaticResourceServlet extends SimpleHttpServlet {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws ServletException, IOException {
        String url = request.getUrl();
        validateStaticFileExists(url);
        ResponseUtil.buildStaticFileResponse(response, url);
    }

    private void validateStaticFileExists(String url) {
        try (final FileInputStream fileStream = new FileInputStream(
                findStaticResourceURL(url).getFile())) {
        } catch (IOException | NullPointerException e) {
            throw new ServletException();
        }
    }

    private URL findStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }
}
