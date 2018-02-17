package io.github.rishikeshdarandale.aws.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author rishikesh
 *
 */
public abstract class AbstractImmutable {
    protected Map<String, List<String>> getMutableMap(Map<String, List<String>> immutableMap) {
        HashMap<String, List<String>> map = new HashMap<>();
        immutableMap.forEach((k, v) -> {
            map.put(k, new ArrayList<>(v));
        });
        return map;
    }

    protected Map<String, List<String>> getImmutableMap(Map<String, List<String>> mutableMap) {
        HashMap<String, List<String>> map = new HashMap<>();
        if (mutableMap != null) {
            mutableMap.forEach((k, v) -> {
                map.put(k, Collections.unmodifiableList(v));
            });
            
        }
        return Collections.unmodifiableMap(map);
    }
}
