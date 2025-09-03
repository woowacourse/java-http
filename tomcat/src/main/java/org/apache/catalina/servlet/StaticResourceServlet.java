package org.apache.catalina.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestInfo.RequestInfo;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.HttpStatus;

public class StaticResourceServlet extends HttpServlet{

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        String requestPath = httpRequest.getRequestPath();

        URL resourceUrl = StaticResourceServlet.class.getClassLoader()
                .getResource("static"+requestPath);
        return resourceUrl != null;
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        String resource = findResource(httpRequest.getRequestPath());
        ContentType contentType = findResourceExtension(httpRequest.getRequestInfo());

        return HttpResponseGenerator.generate(resource, contentType, HttpStatus.OK);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        throw new IllegalArgumentException("[ERROR] 해당 요청을 찾지 못했습니다.");
    }

    private String findResource(String requestPath){
        URL resourceUrl = StaticResourceServlet.class.getClassLoader().getResource("static"+requestPath);

        try {
            Path filePath = Path.of(resourceUrl.toURI());

            return Files.readString(filePath);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    private ContentType findResourceExtension(RequestInfo requestInfo) {
        String extension = requestInfo.getRequestPathExtension();
        return ContentType.findContentType(extension);
    }
}
