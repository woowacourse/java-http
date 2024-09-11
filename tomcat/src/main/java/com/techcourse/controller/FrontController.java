package com.techcourse.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.InvalidResourceException;
import com.techcourse.exception.UnsupportedMethodException;
import com.techcourse.util.FileExtension;
import com.techcourse.util.Resource;

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
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String uri = request.getURI();
        Controller handler = getHandler(uri);
        String fileName = Resource.getFileName(uri);
        if (Objects.isNull(handler) && FileExtension.isFileExtension(fileName)) {
            try {
                getResourceResponse(request, response);
                return;
            } catch (InvalidResourceException e) {
                log.error("Error processing request for endpoint: {}, message: {}", uri, e.getMessage());

                handler = NotFoundController.getInstance();
            }
        }
        if (Objects.isNull(handler)) {
            log.error("Error processing request for endpoint: {}", uri);

            handler = NotFoundController.getInstance();
        }
        try {
            handler.handle(request, response);
        } catch (UnsupportedMethodException e) {
            log.error("Error processing request for endpoint: {}, message: {}", uri, e.getMessage());

            handler = MethodNotAllowedController.getInstance();
            handler.handle(request, response);
        }
    }

    private void getResourceResponse(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = Resource.getFileName(request.getURI());
        ResponseBody responseBody = new ResponseBody(Resource.read(fileName));
        response.setStatus(HttpStatus.OK);
        response.setContentType(MimeType.getMimeType(fileName));
        response.setBody(responseBody);
    }

    private Controller getHandler(String uri) {
        return handlerMappings.get(uri);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: POST");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: GET");
    }
}
