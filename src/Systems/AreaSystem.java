package Systems;

import Data.Area;
import Data.Integer2;

import java.util.HashSet;

public class AreaSystem {
    public static Data.Area InitArea(int occupation){
        var area = new Area();
        area.occupation = occupation;
        area.cells = new HashSet();
        return area;
    }

    public static Data.Area InitArea(int occupation, HashSet<Integer2> cells){
        var area = new Area();
        area.occupation = occupation;
        area.cells = cells;
        return area;
    }


}
