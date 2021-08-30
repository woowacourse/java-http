package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpStatus;

public interface HttpStatusState {

    HttpStatusState state(HttpStatus httpStatus);

    URL resource();
}
