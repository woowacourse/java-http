package nextstep.jwp.web.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;

public interface Controller {

    String doService(HttpRequest httpRequest) throws IOException;

}
