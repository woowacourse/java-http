package cache.com.example.version;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ResourceVersion {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyyMMddHHmmSS";

    private String version;

    @PostConstruct
    public void init() {
        this.version = now(); // 버전은 현재 시각
    }

    public String getVersion() {
        return version;
    }

    private static String now() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        return LocalDateTime.now().format(formatter);
    } // 현재의 시각을 yyyyMMddHHmmSS 식으로 변경해서 version 에다가 명시하게 된다.
}
