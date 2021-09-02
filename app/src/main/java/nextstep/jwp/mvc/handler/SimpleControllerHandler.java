package nextstep.jwp.mvc.handler;

import java.lang.reflect.Method;
import nextstep.jwp.mvc.argument.ArgumentResolverContainer;
import nextstep.jwp.mvc.exception.InvalidReturnTypeException;
import nextstep.jwp.mvc.view.ModelAndView;

public class SimpleControllerHandler extends MethodHandler {

    public SimpleControllerHandler(Method method, Object target,
            ArgumentResolverContainer argumentResolverContainer) {
        super(method, target, argumentResolverContainer);
    }

    @Override
    protected ModelAndView handleReturn(Object returnValue) {
        if(!returnValue.getClass().isAssignableFrom(String.class)) {
            throw new InvalidReturnTypeException();
        }
        return new ModelAndView((String) returnValue);
    }
}
