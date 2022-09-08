package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.ui.Controller;
import nextstep.jwp.ui.FileController;
import org.apache.coyote.http11.request.Path;

public class RequestMapping {

    private static Map<Path, Controller> requestMapper = new HashMap<>();

    private RequestMapping() {
    }

    public static void registerController(Map<String, Controller> controllers) {
        requestMapper = controllers.entrySet().stream()
                .collect(Collectors.toMap(entry -> Path.fromPath(entry.getKey()), Entry::getValue));
    }

    public static Controller getController(Path path) {
        if (path.isFileRequest()) {
            return new FileController();
        }
        if (requestMapper.containsKey(path)) {
            return requestMapper.get(path);
        }
        throw new ResourceNotFoundException();
    }
}
