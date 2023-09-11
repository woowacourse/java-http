package org.apache.coyote.http11.controller;

import common.http.ContentType;
import common.http.Controller;
import common.http.Request;
import common.http.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static common.http.HttpStatus.OK;

public class StaticResourceController implements Controller {

    private static final String STATIC_RESOURCE_DIR = "static";

    @Override
    public void service(Request request, Response response) {
        if (response.hasStaticResourcePath()) {
            buildResponse(response, response.getStaticResourcePath());
            return;
        }

        response.addVersionOfTheProtocol(request.getVersionOfTheProtocol());
        response.addHttpStatus(OK);
        buildResponse(response, request.getPath());
    }

    private void buildResponse(Response response, String path) {
        try {
            URL resource = getResource(path);
            String fileContent = readFileContent(resource);
            ContentType contentType = ContentType.findByPath(path);

            response.addContentType(contentType);

            if (fileContent.length() != 0) {
                response.addBody(fileContent);
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("해당하는 파일을 찾을 수 없습니다.");
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }

    private URL getResource(String path) throws FileNotFoundException {
        ClassLoader classLoader = StaticResourceController.class.getClassLoader();
        URL resource = classLoader.getResource(STATIC_RESOURCE_DIR + path);

        if (resource == null) {
            throw new FileNotFoundException("해당하는 파일을 찾을 수 없습니다.");
        }

        return resource;
    }

    private String readFileContent(URL resource) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resource.getFile())));
    }
}
