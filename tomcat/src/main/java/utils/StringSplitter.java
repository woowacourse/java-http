package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringSplitter {

    private StringSplitter() {
    }

    public static List<String> split(final String delimiter, final String target) {
        if (!target.contains(delimiter)) {
            return List.of(target);
        }
        return List.of(target.split(delimiter));
    }

    public static String getFirst(final String delimiter, final String target) {
        final List<String> units = split(delimiter, target);
        if (units.isEmpty()) {
            return target;
        }
        return units.get(0);
    }

    public static String getLast(final String delimiter, final String target) {
        final List<String> units = split(delimiter, target);
        if (units.isEmpty()) {
            return target;
        }
        return units.get(units.size() - 1);
    }

    public static Map<String, String> getPairs(final String delimiter, final List<String> targets) {
        final Map<String, String> pairs = new HashMap<>();
        for (final String target : targets) {
            final Map<String, String> pair = getPair(delimiter, target);
            pairs.putAll(pair);
        }
        return pairs;
    }

    private static Map<String, String> getPair(final String delimiter, final String target) {
        final int index = target.indexOf(delimiter);

        if (target.contains(delimiter)) {
            final String key = target.substring(0, index);
            final String value = target.substring(index + 1);
            return asPair(key, value);
        }

        final String key = target;
        final String value = "";
        return asPair(key, value);
    }

    private static Map<String, String> asPair(final String key, final String value) {
        final Map<String, String> pair = new HashMap<>();
        pair.put(key, value);
        return pair;
    }
}
