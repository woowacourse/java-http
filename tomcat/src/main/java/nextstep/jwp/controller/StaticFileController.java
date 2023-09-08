package nextstep.jwp.controller;

import java.io.File;
import java.nio.file.Files;
import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MyException;

public class StaticFileController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new MyException(HttpStatus.BAD_REQUEST.code);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        File file = FileFinder.getFile(request.getUri());
        response.setBody(new String(Files.readAllBytes(file.toPath())));
        response.setHttpStatus(HttpStatus.OK);
        response.setHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(ContentType.from(file.getName()).value));
    }
}
