package nextstep.jwp;

import java.io.IOException;

public interface Controller {
    void doProcess(HttpRequest request, HttpResponse response) throws IOException;
}
