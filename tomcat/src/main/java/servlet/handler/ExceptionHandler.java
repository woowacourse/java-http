package servlet.handler;

import java.util.List;
import servlet.mapping.ResponseEntity;

public interface ExceptionHandler {

    void service(Exception e, ResponseEntity entity);

    List<Class<? extends Exception>> getExceptionClass();
}
