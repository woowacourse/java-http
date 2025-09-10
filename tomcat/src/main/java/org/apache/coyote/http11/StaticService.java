package org.apache.coyote.http11;

import org.apache.coyote.http11.parser.HttpResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class StaticService implements org.apache.coyote.http11.service.HttpService {

    @Override
    public void doGet(HttpRequests httpRequests, HttpResponse httpResponse) {
        httpResponse.setContent(getStatic("static" + httpRequests.getHttpRequest()));
        httpResponse.setContentType(createContentType(httpRequests.getHttpRequest()));
        httpResponse.setStatusLine("HTTP/1.1 200 OK");
    }

    private String createContentType(String httpRequest) {
        if (httpRequest.contains("css")) {
            return "text/css;charset=utf-8";
        }
        if (httpRequest.contains("js")) {
            return "application/javascript;charset=utf-8";
        }
        if (httpRequest.contains("html")) {
            return "text/html;charset=utf-8";
        }
        throw new IllegalArgumentException("지원하지 않는 파일 확장자입니다.");
    }

    @Override
    public void doPost(HttpRequests httpRequests, HttpResponse httpResponse) {

    }

    @Override
    public void doUpdate(HttpRequests httpRequests, HttpResponse httpResponse) {

    }

    @Override
    public void doDelete(HttpRequests httpRequests, HttpResponse httpResponse) {

    }

    private byte[] getStatic(String path) {
        URL resource = ClassLoader.getSystemClassLoader()
                .getResource(path);

        if (resource != null) {
            try (FileInputStream fileInputStream = new FileInputStream(resource.getFile())) {
                return fileInputStream.readAllBytes();
            } catch (IOException e) {
                throw new IllegalArgumentException("파일을 찾는데 실패하였습니다.");
            }
        }
        throw new IllegalArgumentException("파일을 찾는데 실패하였습니다.");
    }
}
