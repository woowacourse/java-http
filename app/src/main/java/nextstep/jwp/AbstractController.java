package nextstep.jwp;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
