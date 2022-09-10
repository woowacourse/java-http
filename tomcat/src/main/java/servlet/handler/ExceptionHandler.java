package servlet.handler;

import java.util.List;
import servlet.mapping.ResponseEntity;

public interface ExceptionHandler {

    ResponseEntity service();

    List<Class<? extends Exception>> getExceptionClass();
}
