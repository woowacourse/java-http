package com.techcourse.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.InvalidResourceException;
import com.techcourse.exception.UnsupportedMethodException;
import com.techcourse.util.FileExtension;

public class FrontController extends Controller {
    private static final FrontController instance = new FrontController();
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Map<String, Controller> handlerMappings = new HashMap<>();

    public static FrontController getInstance() {
        return instance;
    }

    private FrontController() {
        initHandlerMappings();
    }

    private void initHandlerMappings() {
        handlerMappings.put("/login", LoginController.getInstance());
        handlerMappings.put("/register", RegisterController.getInstance());
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        Controller handler = getHandler(request.getURI());
        String fileName = getFileName(request.getURI());
        if (Objects.isNull(handler) && FileExtension.isFileExtension(fileName)) {
            try {
                HttpResponse response = new HttpResponse();
                ResponseBody responseBody = new ResponseBody(readResource(fileName));
                response.setStatus(HttpStatus.OK);
                response.setContentType(MimeType.getMimeType(fileName));
                response.setBody(responseBody);
                return response;
            } catch (InvalidResourceException e) {
                log.error("Error processing request for endpoint: {}", request.getURI());

                handler = NotFoundController.getInstance();
            }
        }
        if (Objects.isNull(handler)) {
            log.error("Error processing request for endpoint: {}", request.getURI());

            handler = NotFoundController.getInstance();
        }
        try {
            HttpResponse response = handler.handle(request);
            return response;
        } catch (UnsupportedMethodException e) {
            log.error("Error processing request for endpoint: {}", request.getURI());

            handler = MethodNotAllowedController.getInstance();
            HttpResponse response = handler.handle(request);
            return response;
        }
    }

    private Controller getHandler(String uri) {
        return handlerMappings.get(uri);
    }

    private String getFileName(String endpoint) {
        int index = endpoint.indexOf("?");
        String path = endpoint;
        if (index != -1) {
            path = path.substring(0, index);
        }
        String fileName = path.substring(1);
        if (fileName.isEmpty()) {
            fileName = "hello.html";
        }
        return fileName;
    }

    private String readResource(String fileName) throws IOException {
        URL resource = findResource(fileName);
        if (Objects.isNull(resource)) {
            throw new InvalidResourceException("Cannot find resource with name: " + fileName);
        }
        Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private URL findResource(String fileName) {
        return getClass().getClassLoader().getResource("static/" + fileName);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }
}
