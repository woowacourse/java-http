package nextstep.jwp.http.controller;

import java.io.File;

public abstract class StandardController implements Controller {
    protected File toFile(String path) {
        path = ClassLoader.getSystemResource(path).getPath();
        return new File(path);
    }
}
