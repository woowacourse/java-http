package support;

import java.util.HashMap;
import java.util.Map;

/**
 * static 유틸 클래스로 생성할 수 없는 객체는 이곳에 저장하여 싱글톤으로 관리합니다.
 */
public class ApplicationContainer {

    private final Map<Class, Object> container = new HashMap<>();

    public ApplicationContainer() {
        container.put(FileUtils.class, new FileUtils());
    }

    public <T> T getSingletonObject(Class<T> clazz) {
        return (T) container.get(clazz);
    }
}
