package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

public class ResponseProcessor {

    private static final String STATIC_PATH = "static/";

    private final Path path;
    private final QueryParameters queryParameters;
    private final String responseBody;

    public ResponseProcessor(StartLine startLine) throws URISyntaxException, IOException {
        String uri = startLine.getUri();
        this.path = new Path(uri);
        this.queryParameters = new QueryParameters(uri);
        this.responseBody = processResponseBody();
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + path.extractContentType() + ";charset=utf-8 ",
                "Content-Length: " + extractContentLength() + " ",
                "",
                responseBody);
    }

    private String processResponseBody() throws URISyntaxException, IOException {
        if (path.isFileRequest()) {
            String fileName = path.getFileName();
            return readFile(fileName);
        }
        if (path.isLogin()) {
            String fileName = path.getFileName().concat(".html");
            checkUser();
            return readFile(fileName);
        }
        return "Hello world!";
    }

    private String readFile(String fileName) throws URISyntaxException, IOException {
        final URI fileUri = getClass().getClassLoader().getResource(STATIC_PATH + fileName).toURI();
        byte[] lines = Files.readAllBytes(Paths.get(fileUri));
        return new String(lines);
    }

    private int extractContentLength() {
        return responseBody.getBytes().length;
    }

    private void checkUser() {
        if (queryParameters.isEmpty()) {
            return;
        }
        User user = InMemoryUserRepository.findByAccount(queryParameters.getAccount())
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(queryParameters.getPassword())) {
            throw new AuthenticationException();
        }
        System.out.println(user);
    }
}
