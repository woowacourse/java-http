package org.apache.catalina.controller;

import com.techcourse.controller.LoginController;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Status;

public class AbstractController implements Controller {

    @Override
    public Http11Response service(final Http11Request http11Request) throws IOException {
        if(http11Request.getMethod().equals("GET")) {
            return doGet(http11Request);
        }
        if(http11Request.getMethod().equals("POST")) {
            return doPost(http11Request);
        }

        return createRedirect401Response();
    }

    protected Http11Response doPost(Http11Request http11Request) throws IOException {
        return createRedirect401Response();
    }

    protected Http11Response doGet(Http11Request http11Request) throws IOException {
        String path = http11Request.getPath();
        String contentType = guessContentTypeByFileExtension(path);

        byte[] body = readFromResourcePath(path);

        return new Http11Response(body, contentType, Http11Status.OK);
    }

    protected String guessContentTypeByFileExtension(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";

        return "text/html";
    }

    protected byte[] readFromResourcePath(final String resourcePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("static/");

        stringBuilder.append(resourcePath);
        if(!resourcePath.contains(".")){
            stringBuilder.append(".html");
        }

        String classPath = stringBuilder.toString();
        try (InputStream resourceAsStream = getClass().
                getClassLoader().
                getResourceAsStream(classPath)) {
            if(resourceAsStream == null) {
                return null;
            }

            return resourceAsStream.readAllBytes();
        }
    }

    private Http11Response createRedirect401Response() throws IOException {
        String failResourceName = "401.html";
        String contentType = guessContentTypeByFileExtension(failResourceName);
        byte[] body = readFromResourcePath(failResourceName);

        return new Http11Response(body, "text/html", Http11Status.NOT_FOUND);
    }
}
