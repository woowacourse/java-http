package nextstep.jwp.handler;

import com.sun.jdi.InternalException;
import java.util.Arrays;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;

public class ExceptionHandler {

    public HttpResponse handle(DashboardException exception) {
        HttpStatus httpStatus = Arrays.stream(HttpStatus.values())
            .filter(it -> it.code == exception.getStatusCode())
            .findFirst()
            .orElseThrow(() -> new InternalException());
        HttpResponse response = new HttpResponse();
        response.toRedirect(String.format("/%s.html", httpStatus.code));
        return response;
    }

    public HttpResponse handleInternalServerError() {
        HttpResponse response = new HttpResponse();
        response.toRedirect("/500.html");
        return response;
    }
}
