package nextstep.jwp.mvc.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParameterClass {

    private final Class<?> parameterClass;
    private final List<Method> setters;

    public ParameterClass(Class<?> parameter) {
        this.parameterClass = parameter;
        this.setters = setters();
    }

    private List<Method> setters() {
        return Arrays.stream(parameterClass.getMethods())
                .filter(method -> method.getName().startsWith("set"))
                .collect(Collectors.toList());
    }

    public Object createInstance(Map<String, String> requestParams) {
        try {
            final Object target = parameterClass.getConstructor((Class<?>[]) null).newInstance();
            for (Method setter : setters) {
                char[] arr = setter.getName().substring(3).toCharArray();
                arr[0] = Character.toLowerCase(arr[0]);
                final String setterName = new String(arr);
                final String value = requestParams.get(setterName);
                setter.invoke(target, value);
            }
            return target;
        } catch (Exception e) {
            throw new IllegalStateException("can not create object");
        }
    }

    public boolean isTypeOf(Class<?> aClass) {
        return parameterClass.isAssignableFrom(aClass);
    }
}
