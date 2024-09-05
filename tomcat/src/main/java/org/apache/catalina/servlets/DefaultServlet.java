package org.apache.catalina.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.core.request.HttpRequest;
import org.apache.catalina.core.response.HttpResponse;

/**
 * 정적 리소스를 관리하는 서블릿입니다.
 */
public class DefaultServlet implements Servlet {

    private final Map<String, String> contentTypes = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css",
            "js", "application/javascript"
    );

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {

        URL resource = getClass().getClassLoader().getResource("static" + request.getRequestURI());

        String fileContent = getFileContent(resource);
        String resourceType = getResourceType(resource);
        String contentType = contentTypes.getOrDefault(resourceType, "text/html;charset=utf-8");

        response.setContentType(contentType);
        response.setContentLength(fileContent.getBytes().length);
        response.setResponseBody(fileContent);
    }

    private String getFileContent(URL resourceURL) throws IOException {
        if (resourceURL != null) {
            File file = new File(resourceURL.getFile());
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String collect = reader.lines().collect(Collectors.joining("\n"));
                return collect + "\n";
            } catch (IOException e) {
                return "Hello world!";
            }
        }
        return "";
    }

    private String getResourceType(URL resourceURL) {
        return resourceURL.getPath().substring(resourceURL.getPath().lastIndexOf('.') + 1);
    }
}
