package nextstep.jwp.response;

import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.exception.PageNotFoundError;
import nextstep.jwp.mvc.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseBody {

    private static final String DEFAULT_STATIC_RESOURCE_PATH = "static/";

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private final String content;
    private final int contentLength;

    public ResponseBody(String content, int contentLength) {
        this.content = content;
        this.contentLength = contentLength;
    }

    public static ResponseBody of(String contentName) {
        String content = findContent(contentName);
        return new ResponseBody(content, content.getBytes().length);
    }

    public static ResponseBody empty() {
        return new ResponseBody("", 0);
    }

    private static String findContent(String contentName) {
        try {
            String requestResourceName = DEFAULT_STATIC_RESOURCE_PATH + contentName;
            URL resource = ResponseBody.class.getClassLoader().getResource(requestResourceName);
            Path resourcePath = new File(resource.getFile()).toPath();
            return new String(Files.readAllBytes(resourcePath));
        } catch (IOException e) {
            logger.error("getContent Error", e);
            throw new InternalServerError();
        } catch (NullPointerException e) {
            logger.error("cannot found resource", e);
            throw new PageNotFoundError();
        }
    }

    public String toResponseMessageBody() {
        return this.content;
    }

    public String getContent() {
        return content;
    }

    public int getContentLength() {
        return contentLength;
    }
}
