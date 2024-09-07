package org.apache.coyote.controller;

import com.techcourse.controller.GetLoginController;
import com.techcourse.controller.GetRegisterController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.PostLoginController;
import com.techcourse.controller.PostRegisterController;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestKey;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.util.FileExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);
    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public FrontController() {
        controllers.put(new RequestKey(HttpMethod.GET, "/login"), new GetLoginController());
        controllers.put(new RequestKey(HttpMethod.POST, "/login"), new PostLoginController());
        controllers.put(new RequestKey(HttpMethod.GET, "/register"), new GetRegisterController());
        controllers.put(new RequestKey(HttpMethod.POST, "/register"), new PostRegisterController());
    }

    public HttpResponse dispatch(HttpRequest request) {
        log(request);
        String path = request.getPath();
        if (FileExtension.isFileExtension(path)) {
            String resourcePath = "static" + path;
            try {
                Path filePath = Paths.get(getClass().getClassLoader().getResource(resourcePath).toURI());
                MimeType mimeType = MimeType.from(FileExtension.from(path));
                byte[] body = Files.readAllBytes(filePath);
                ResponseHeader header = new ResponseHeader();
                header.setContentType(mimeType);
                return new HttpResponse(HttpStatusCode.OK, header, body);
            } catch (URISyntaxException | IOException e) {
                ResponseHeader header = new ResponseHeader();
                header.setContentType(MimeType.OTHER);
                return new HttpResponse(HttpStatusCode.OK, header, "No File Found".getBytes());
            }
        }
        Controller controller = getController(request.getMethod(), path);
        return controller.run(request);
    }

    private void log(HttpRequest request) {
        log.info("method = {}, path = {}, url = {}", request.getMethod(), request.getPath(), request.getUrl());
        for (String key : request.getQueryParams().keySet()) {
            log.info("key = {}, value = {}", key, request.getQueryParams().get(key));
        }
        for (String key : request.getHeaders().getCookies().getCookies().keySet()) {
            log.info("COOKIE: key = {}, value = {}", key, request.getHeaders().getCookies().getCookies().get(key));
        }
        if (!request.isBodyEmpty()) {
            log.info(request.getBody());
        }
    }

    public Controller getController(HttpMethod method, String path) {
        Controller controller = controllers.get(new RequestKey(method, path));
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
