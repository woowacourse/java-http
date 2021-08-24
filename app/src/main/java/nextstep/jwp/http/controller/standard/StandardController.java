package nextstep.jwp.http.controller.standard;

import java.io.File;
import nextstep.jwp.http.controller.Controller;

public abstract class StandardController implements Controller {
    protected File toFile(String path) {
        path = ClassLoader.getSystemResource(path).getPath();
        return new File(path);
    }
}
