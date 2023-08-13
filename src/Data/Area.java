package Data;

import java.util.HashSet;

public class Area {
    public int occupation;
    public HashSet<Integer2> cells;

    @Override
    public String toString(){
        return "(Occupation: " + occupation + "/Size: " + cells.size() + ")";
    }
}
