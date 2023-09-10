package nextstep.jwp.controller;

import java.io.File;
import java.nio.file.Files;
import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.DashboardException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticFileController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new DashboardException(HttpStatus.METHOD_NOT_ALLOWED.code);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        File file = FileFinder.getFile(request.getPath());
        response.setBody(new String(Files.readAllBytes(file.toPath())), ContentType.from(file.getName()));
        response.setHttpStatus(HttpStatus.OK);
    }
}
