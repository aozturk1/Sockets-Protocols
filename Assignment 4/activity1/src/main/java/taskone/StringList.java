package taskone;

import java.util.List;
import java.util.ArrayList;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }

    public void clear() {
        strings.clear();
    }

    public String find(String str) {
        return String.valueOf(strings.indexOf(str));
    }

    public void delete(int str) {
        strings.remove(str);
    }

    public void prepend(String str) {
        String[] strArray = str.split(" ");
        String str2 = strings.get(Integer.parseInt(strArray[0])) + strArray[1];
        strings.add(Integer.parseInt(strArray[0]), str2);
        strings.remove(Integer.parseInt(strArray[0])+1);
    }

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }

    public int size() {
        return strings.size();
    }

    public String toString() {
        return strings.toString();
    }
}