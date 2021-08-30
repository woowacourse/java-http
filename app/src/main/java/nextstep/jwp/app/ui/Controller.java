package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request) throws IOException;
}
