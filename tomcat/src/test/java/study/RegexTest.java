package study;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class RegexTest {

    private final Pattern pathPattern = Pattern.compile("(?<path>/[a-zA-Z0-9\\-]*)[?]?");
    private final Pattern queryParameterPattern = Pattern.compile("((?<field>[a-zA-Z0-9\\-]+)=(?<value>[a-zA-Z0-9\\-]+))&?");


    @Test
    void findPathByRegex() {
        // given
        Matcher matcher = pathPattern.matcher("/hello?a=1");
        // when
        boolean b = matcher.find();
        System.out.println("b = " + b);
        String group = matcher.group("path");
        System.out.println("group = " + group);
        // then
//        System.out.println("group = " + group);
    }

    @Test
    void asdasd() {
        // given
        URL resource = Thread.currentThread().getContextClassLoader().getResource("XXXXX123123");
        System.out.println("resource = " + resource);
        File file = new File(resource.getFile());
        // when
        System.out.println(file);

        // then
    }

    @Test
    void findQueryParamByRegex() {
        // given
        Matcher matcher = queryParameterPattern.matcher("/hi?hello=world&multiple=param");
        // when

        while (matcher.find()) {
            System.out.println("matcher.group(\"field\") = " + matcher.group("field"));
            System.out.println("matcher.group(\"value\") = " + matcher.group("value"));
        }
        // then
        System.out.println("matcher.groupCount() = " + matcher.groupCount());
    }
}
