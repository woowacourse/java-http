package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

public class RequestToResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestToResponse.class);

    private static final String STATIC = "static";
    private static final String REDIRECT = "/index.html";

    public String build(HttpRequest request) throws IOException {
        Path path = request.getRequestLine().getPath();

        if (path.getPath().equals("/")) {
            return HttpResponse.basicResponse().toResponse();
        }

        if (path.getPath().startsWith("/login")) {
            return login(request.getRequestLine());
        }

        final URL resource = getClass().getClassLoader().getResource(STATIC.concat(path.getPath()));
        try {
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.getContentTypeFromExtension(path.getPath()));
            header.setContentLength(responseBody.getBytes().length);

            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        } catch (NullPointerException e) {
            return HttpResponse.notFoundResponses().toResponse();
        }
    }

    private String login(RequestLine requestLine) throws IOException {
        Path path = requestLine.getPath();
        final URL resource = getClass().getClassLoader().getResource(STATIC.concat(path.getPath()).concat(MimeType.HTML.getExtension()));
        try {
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.HTML.getContentType());
            header.setContentLength(responseBody.getBytes().length);

            if (path.hasQueryParameter()) {
                StatusLine statusLine = new StatusLine(HttpStatus.FOUND);
                Map<String, String> parameters = path.getParameters();
                User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                        .orElseThrow();
                if (user.checkPassword(parameters.get("password"))) {
                    log.info(user.toString());
                    header.setLocation(REDIRECT);
                    HttpResponse response = new HttpResponse(statusLine, header, null);
                    return response.toResponse();
                }
                header.setLocation("/401.html");
                HttpResponse response = new HttpResponse(statusLine, header, null);
                return response.toResponse();

            }
            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        } catch (NullPointerException e) {
            return HttpResponse.notFoundResponses().toResponse();
        }
    }
}
