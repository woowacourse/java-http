package nextstep.jwp.controller;

import javax.naming.ldap.Control;
import nextstep.jwp.HttpRequest;

public interface Controller {

    boolean isMatchingController(HttpRequest httpRequest);

    String doService(HttpRequest httpRequest);

}
