package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.coyote.http11.Http11Processor;
import org.apache.exception.DataNotFoundException;
import org.apache.exception.InvalidRequestException;
import org.apache.exception.RequestProcessingException;
import org.apache.http.ContentType;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getUri().equals("/login")
                && request.checkQueryStringExistence("account")
                && request.checkQueryStringExistence("password");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        try {
            URL resource = findResourceUrl("/login.html");
            if (resource == null) {
                throw new RequestProcessingException("URI가 올바르지 않습니다.");
            }

            User user = findUser(request.getQueryString("account"));
            log.info("user : {}", user);

            Path path = Paths.get(resource.toURI());
            String responseBody = Files.readString(path);

            response.setStatusCode(StatusCode.OK);
            response.setHeader("Content-Type", getFileExtension(path).getValue());
            response.setBody(responseBody);

        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            throw new RequestProcessingException("리소스를 읽는데 오류가 발생했습니다.");
        }
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new DataNotFoundException("해당 유저가 존재하지 않습니다."));
    }

    private URL findResourceUrl(String uri) {
        List<String> uriPart = List.of(uri.split("/"));
        if (uriPart.contains(".") || uriPart.contains("..")) {
            throw new InvalidRequestException("올바르지 않은 리소스 주소입니다.");
        }

        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResource("static" + uri);
    }

    private ContentType getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        List<String> split = List.of(fileName.split("\\."));
        return ContentType.parse(split.getLast());
    }
}
