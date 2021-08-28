package nextstep.jwp;

import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;

public interface Controller {

    String getResource();

    HttpResponse execute(HttpRequest httpRequest);
}
