package taskone;

import java.util.List;
import java.util.ArrayList;

public class StringList {
    
    List<String> strings = new ArrayList<String>();

    public boolean add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
            return true;
        } else {
            return false;
        }
    }

    public boolean clear() {
        if(strings.isEmpty()) {
            return false;
        } else {
            strings.clear();
            return true;
        }
    }

    public String find(String str) {
        return String.valueOf(strings.indexOf(str));
    }

    synchronized public void delete(int index) {
        if (index >= 0 && index < strings.size()) {
            strings.remove(index);
        }
    }

    synchronized public void prepend(String str) {
        String[] strArray = str.split(" ");
        int index = Integer.parseInt(strArray[0]);
        if (index >= 0 && index < strings.size()) {
            String oldString = strings.get(index);
            strings.set(index, strArray[1] + oldString);
        }
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