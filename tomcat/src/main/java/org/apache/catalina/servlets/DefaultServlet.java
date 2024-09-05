package org.apache.catalina.servlets;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.core.HttpResponse;

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
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getRequestURI());

        String fileContent = getFileContent(resource);
        String resourceType = getResourceType(resource);
        String contentType = contentTypes.getOrDefault(resourceType, "text/html;charset=utf-8");

        servletResponse.setContentType(contentType);
        servletResponse.setContentLength(fileContent.getBytes().length);
        setResponseBody(servletResponse, fileContent);
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

    private void setResponseBody(ServletResponse response, String fileContent) {
        HttpResponse httpResponse = (HttpResponse) response;
        httpResponse.setResponseBody(fileContent);
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {

    }
}
