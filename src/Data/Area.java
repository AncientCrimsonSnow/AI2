package Data;

import Utils.int2;

import java.util.HashSet;

public class Area {
    public int occupation;
    public HashSet<int2> cells;

    @Override
    public String toString(){
        return "(Occupation: " + occupation + "/Size: " + cells.size() + ")";
    }
}
