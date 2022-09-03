package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.UserRequest;

import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.apache.coyote.http11.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String RESOURCE_PATH = "static";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String EXTENSION_DELIMITER = ".";

    private final String path;

    public ResponseHandler(final String path) {
        this.path = path;
    }

    public String getResponse() throws IOException, FileNotFoundException {
        final ResponseHeader responseHeader = new ResponseHeader(path);
        final String responseBody = getResponseBody();
        return  String.join("\r\n", responseHeader.getHeader(responseBody), responseBody);
    }

    private String getResponseBody() throws FileNotFoundException, IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        if (path.contains("/login") && QueryParam.isQueryParam(path)) {
            return getLoginContent();
        }
        return getContent(path);
    }

    private String getContent(String path) throws FileNotFoundException, IOException {
        final URL resource = getClass()
                .getClassLoader()
                .getResource(RESOURCE_PATH + path + getExtension());
        if (resource == null) {
            throw new FileNotFoundException();
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getExtension() {
        if (path.contains(EXTENSION_DELIMITER)) {
            return "";
        }
        return EXTENSION_DELIMITER + DEFAULT_EXTENSION;
    }

    private String getLoginContent() throws FileNotFoundException, IOException {
        final QueryParam queryParam = new QueryParam(path);
        if (queryParam.matchParameters("account") && queryParam.matchParameters("password")) {

            UserRequest userRequest = new UserRequest(queryParam.getValue("account"),
                    queryParam.getValue("password"));

            final User user = InMemoryUserRepository.findByAccount(userRequest.getAccount())
                    .orElseThrow(UserNotFoundException::new);

            LOGGER.info(user.toString());
            return getContent("/login");
        }
        throw new QueryParamNotFoundException();
    }
}
