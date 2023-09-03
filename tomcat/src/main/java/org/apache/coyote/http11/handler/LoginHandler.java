package org.apache.coyote.http11.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.LoginService;

public class LoginHandler implements Handler {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    @Override
    public boolean supports(HttpRequest request) {
        return request.getUrl().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> queryString = request.getQueryString();
        if (queryString.size() == 0) {
            String url = "/login.html";
            try (
                    FileInputStream fileStream = new FileInputStream(
                            findStaticResourceURL(url).getFile())
            ) {
                String path = findStaticResourceURL(url).getFile();
                String extension = getResourceExtension(path);
                return new HttpResponse.Builder()
                        .setHttpStatusCode(HttpStatusCode.OK)
                        .setContentType(toTextContentType(extension))
                        .setBody(fileStream.readAllBytes())
                        .build();
            } catch (IOException | NullPointerException e) {
                throw new RuntimeException();
            }
        }
        return LoginService.login(request);
    }

    private URL findStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }

    private String getResourceExtension(String path) {
        return path.split("\\.")[1];
    }

    private String toTextContentType(String extension) {
        return "text/" + extension;
    }
}
