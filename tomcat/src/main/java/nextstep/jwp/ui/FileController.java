package nextstep.jwp.ui;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class FileController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Path path = request.getPath();
        response.initResponseValues(ResponseEntity.body(path.getFileName()));
    }
}
