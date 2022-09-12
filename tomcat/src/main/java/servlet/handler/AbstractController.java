package servlet.handler;

import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;
import servlet.mapping.ResponseEntity;

public class AbstractController implements Controller {

    private final String ERROR_405_MESSAGE = "매핑되는 메소드가 없습니다.";
    private final String path;

    public AbstractController(String path) {
        this.path = path;
    }

    @Override
    public ResponseEntity service(HttpRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return doGet(request);
        }
        if (request.getMethod() == HttpMethod.POST) {
            return doPost(request);
        }
        throw new MethodNotAllowedException(ERROR_405_MESSAGE);
    }

    public String getPath() {
        return path;
    }


    protected ResponseEntity doPost(HttpRequest request) {
        throw new MethodNotAllowedException(ERROR_405_MESSAGE);
    }

    protected ResponseEntity doGet(HttpRequest request) {
        throw new MethodNotAllowedException(ERROR_405_MESSAGE);
    }
}
