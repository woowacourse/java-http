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
        Controller handler = getHandler(request.getURI());
        String fileName = Resource.getFileName(request.getURI());

        try {
            if (Objects.isNull(handler) && FileExtension.isFileExtension(fileName)) {
                handleStaticResource(request, response);
                return;
            }
            handler.handle(request, response);
        } catch (InvalidResourceException e) {
            log.error("Error processing request for endpoint: {}, message: {}", uri, e.getMessage());

            handleNotFound(request, response);
        } catch (UnsupportedMethodException e) {
            log.error("Unsupported method for endpoint: {}, message: {}", uri, e.getMessage());

            handleUnsupportedMethod(request, response);
        }
    }

    private Controller getHandler(String uri) {
        return handlerMappings.get(uri);
    }

    private void handleStaticResource(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = Resource.getFileName(request.getURI());
        ResponseBody responseBody = new ResponseBody(Resource.read(fileName));
        response.setStatus(HttpStatus.OK);
        response.setContentType(MimeType.getMimeType(fileName));
        response.setBody(responseBody);
    }

    private void handleUnsupportedMethod(HttpRequest request, HttpResponse response) throws IOException {
        Controller methodNotAllowedController = MethodNotAllowedController.getInstance();
        methodNotAllowedController.handle(request, response);
    }

    private void handleNotFound(HttpRequest request, HttpResponse response) throws IOException {
        Controller notFoundController = NotFoundController.getInstance();
        notFoundController.handle(request, response);
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
