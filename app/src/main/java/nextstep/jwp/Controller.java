package nextstep.jwp;

import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;

import java.io.IOException;

public interface Controller {

    public String getResource();

    public HttpResponse doGet(HttpRequest httpRequest) throws IOException;
}
