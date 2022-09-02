package support;

import java.lang.reflect.Method;

public class DynamicMethodInvoke {

    private Class<?> objectType;
    private String methodName;
    private Object[] parameters;
    private Class[] parameterTypes;

    private DynamicMethodInvoke() {
    }

    /**
     * Map returnType =
     *      (Map) ReflectionUtils
     *          .builder()
     *          .objectType(Http11Processor.class)
     *          .methodName("parseUserMap")
     *          .parameters("/login?account=philz&password=1234", "")
     *          .execute();
     */

    public static DynamicMethodInvoke builder() {
        return new DynamicMethodInvoke();
    }

    public DynamicMethodInvoke objectType(Class<?> objectType) {
        this.objectType = objectType;
        return this;
    }

    public DynamicMethodInvoke methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public DynamicMethodInvoke parameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }

    public Object execute() {
        try {
            final Object object = objectType.getDeclaredConstructor().newInstance();
            parameterTypes = new Class[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                parameterTypes[i] = parameters[i].getClass();
            }

            final Method method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);

            return method.invoke(object, parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
