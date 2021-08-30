package nextstep.jwp.controller;

import nextstep.jwp.http.CustomException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.FileReaderInStaticFolder;

public class NoController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        renderNotFoundPage(response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        renderNotFoundPage(response);
    }

    private void renderNotFoundPage(HttpResponse response) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setBody(fileReaderInStaticFolder.read("404.html"));
    }

    @Override
    public boolean isSatisfiedBy(String httpUriPath) {
        throw new CustomException("존재하지 않는 컨트롤러입니다.");
    }
}
