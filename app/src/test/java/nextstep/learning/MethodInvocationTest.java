package nextstep.learning;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodInvocationTest {

    @DisplayName("Object 배열의 형식으로 메서드의 매개변수 값을 넘길 수 있다.")
    @Test
    void invokeWithParameter() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = MethodInvocationTest.class.getMethod("textNumberCombine", String.class, Integer.class);
        final Object[] parameter = new Object[2];
        parameter[0] = "String";
        parameter[1] = 1000;

        final Object result = method.invoke(new MethodInvocationTest(), parameter);
        assertThat((String) result).isEqualTo("String&&1000");
    }

    public String textNumberCombine(String text, Integer number) {
        return text + "&&" + number;
    }
}
