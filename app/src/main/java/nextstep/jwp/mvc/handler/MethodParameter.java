package nextstep.jwp.mvc.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

public class MethodParameter {

    private Parameter parameter;
    private int parameterOrder;
    private Annotation[] annotations;
    private ParameterClass parameterClass;

    public MethodParameter(Parameter parameter, int parameterOrder) {
        this.parameter = parameter;
        this.parameterOrder = parameterOrder;
        this.annotations = parameter.getDeclaredAnnotations();
        this.parameterClass = new ParameterClass(parameter.getClass());
    }

    public boolean hasAnnotationType(Class<? extends Annotation> annotationType) {
        return Arrays.stream(annotations).anyMatch(annotationType::isInstance);
    }

    public Object createInstance(Map<String, String> requestParams) {
        return parameterClass.createInstance(requestParams);
    }

    public int getParameterOrder() {
        return parameterOrder;
    }
}
