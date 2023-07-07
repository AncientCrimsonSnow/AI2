package Core;

import Utils.int2;

import java.util.HashSet;

public class Area {
    public int occupation = -1;
    public HashSet<int2> cells = new HashSet();

    @Override
    public String toString(){
        return "(" + occupation + " Size: " + cells.size() + ")";
    }
}
