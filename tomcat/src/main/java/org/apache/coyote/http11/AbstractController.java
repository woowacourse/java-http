package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.isGet()) {
            doGet(request, response);
        }

        if (httpMethod.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected byte[] readResource(String resourceName) {
        String resourcePath = getClass().getClassLoader().getResource("static/" + resourceName).getPath();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath))) {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
