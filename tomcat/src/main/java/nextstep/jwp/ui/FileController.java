package nextstep.jwp.ui;

import static org.apache.coyote.http11.response.ResponseBody.STATIC_PATH;

import java.net.URL;
import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class FileController implements Controller {

    private static FileController INSTANCE;

    public static Controller getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new FileController();
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Path path = request.getPath();
        String fileName = path.getFileName();
        URL resource = getClass().getClassLoader().getResource(STATIC_PATH + fileName);
        if (resource == null) {
            throw new ResourceNotFoundException();
        }
        response.initResponseValues(request, ResponseEntity.body(fileName));
    }
}
