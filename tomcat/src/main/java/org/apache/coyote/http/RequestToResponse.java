package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.request.HttpMethod;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestToResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestToResponse.class);

    private static final String STATIC = "static";
    private static final String REDIRECT = "/index.html";
    private static final String UNAUTHORIZED = "/401.html";

    public String build(HttpRequest request) throws IOException {
        Path path = request.getRequestLine().getPath();

        if (path.getPath().equals("/")) {
            return HttpResponse.basicResponse().toResponse();
        }

        if (path.getPath().startsWith("/login")) {
            return login(request);
        }

        if (path.getPath().startsWith("/register")) {
            return register(request);
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

    private String login(HttpRequest request) throws IOException {
        RequestLine requestLine = request.getRequestLine();
        Path path = requestLine.getPath();
        final URL resource = getClass().getClassLoader().getResource(STATIC.concat(path.getPath()).concat(MimeType.HTML.getExtension()));
        try {
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.HTML.getContentType());
            header.setContentLength(responseBody.getBytes().length);

            if (requestLine.getMethod().equals(HttpMethod.GET)) {
                StatusLine statusLine = new StatusLine(HttpStatus.OK);
                HttpResponse response = new HttpResponse(statusLine, header, responseBody);
                return response.toResponse();
            }

            if (requestLine.getMethod().equals(HttpMethod.POST)) {
                StatusLine statusLine = new StatusLine(HttpStatus.FOUND);
                String body = request.getBody();
                List<String> bodies = List.of(body.split("&"));
                Map<String, String> parsedBody = bodies.stream()
                        .map(b -> b.split("="))
                        .collect(Collectors.toMap(b -> b[0], b -> b[1]));
                return InMemoryUserRepository.findByAccount(parsedBody.get("account"))
                        .map(user -> getLoginResult(user, parsedBody, header, statusLine))
                        .orElse(new HttpResponse(new StatusLine(HttpStatus.OK), header, responseBody).toResponse());
            }
        } catch (NullPointerException e) {
            return HttpResponse.notFoundResponses().toResponse();
        }
        return HttpResponse.notFoundResponses().toResponse();
    }

    private static String getLoginResult(User user, Map<String, String> parsedBody, ResponseHeader header, StatusLine statusLine) {
        if (user.checkPassword(parsedBody.get("password"))) {
            log.info(user.toString());
            header.setLocation(REDIRECT);
            HttpResponse response = new HttpResponse(statusLine, header, null);
            return response.toResponse();
        }
        header.setLocation(UNAUTHORIZED);
        HttpResponse response = new HttpResponse(statusLine, header, null);
        return response.toResponse();
    }

    private String register(HttpRequest request) throws IOException {
        RequestLine requestLine = request.getRequestLine();
        Path path = requestLine.getPath();
        final URL resource = getClass().getClassLoader().getResource(STATIC.concat(path.getPath()).concat(MimeType.HTML.getExtension()));
        try {
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.HTML.getContentType());
            header.setContentLength(responseBody.getBytes().length);

            if (requestLine.getMethod().equals(HttpMethod.GET)) {
                StatusLine statusLine = new StatusLine(HttpStatus.OK);
                HttpResponse response = new HttpResponse(statusLine, header, responseBody);
                return response.toResponse();
            }

            if (requestLine.getMethod().equals(HttpMethod.POST)) {
                String body = request.getBody();
                List<String> bodies = List.of(body.split("&"));
                Map<String, String> parsedBody = bodies.stream()
                        .map(b -> b.split("="))
                        .collect(Collectors.toMap(b -> b[0], b -> b[1]));
                InMemoryUserRepository.save(new User(parsedBody.get("account"), parsedBody.get("password"), parsedBody.get("email")));
                StatusLine statusLine = new StatusLine(HttpStatus.FOUND);
                header.setLocation(REDIRECT);
                HttpResponse response = new HttpResponse(statusLine, header, null);
                return response.toResponse();
            }
        } catch (NullPointerException e) {
            return HttpResponse.notFoundResponses().toResponse();
        }
        return HttpResponse.notFoundResponses().toResponse();
    }
}
