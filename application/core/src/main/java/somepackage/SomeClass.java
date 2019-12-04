package somepackage;

import java.util.Iterator;
import java.util.Map;

public class SomeClass {
    public static void main(String[] args) {
        Map<String, String> env = System.getenv();

        Iterator<Map.Entry<String, String>> iterator = env.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, String> stringStringEntry = iterator.next();

            System.out.println(stringStringEntry.getKey() + " -> " + stringStringEntry.getValue());
        }
    }

}
