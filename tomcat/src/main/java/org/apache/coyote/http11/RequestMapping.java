package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.Path;

public class RequestMapping {

    private final Map<Path, Controller> requestMapper = new HashMap<>();
    private Controller fileController;

    public RequestMapping() {
    }

    public void registerController(String path, Controller controller) {
        requestMapper.put(Path.fromPath(path), controller);
    }

    public Controller findController(Path path) {
        if (requestMapper.containsKey(path)) {
            return requestMapper.get(path);
        }
        return fileController;
    }

    public void setFileController(Controller controller) {
        this.fileController = controller;
    }
}
