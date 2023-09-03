package nextstep.servlet.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request) throws IOException;
}
