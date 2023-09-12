package nextstep.jwp.servlets;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.exception.HttpMethodNotAllowedException;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class HttpServlet implements Servlet {

    private static final String RESOURCE_BASE_PATH = "static/";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        throw new HttpMethodNotAllowedException();
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new HttpMethodNotAllowedException();
    }

    protected String findResourceWithPath(String absolutePath) throws IOException {
        URL resourceUrl = ClassLoader.getSystemResource(RESOURCE_BASE_PATH + absolutePath);
        File file = new File(resourceUrl.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
