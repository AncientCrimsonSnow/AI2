package Data;

import Utils.int2;

import java.util.HashSet;
import java.util.Map;

public class Board {
    public static final int ScaleFactor = 2;
    public static final int DEFAULT_BOARD_SIDE_LENGTH = 256;
    public static final float SCALE = (float) (1/Math.pow(2, ScaleFactor));
    public static final int BOARD_SIDE_LENGTH = Math.round(DEFAULT_BOARD_SIDE_LENGTH * SCALE);

    public Game game;
    public Map<int2, Area> cellAreaMap;
    public HashSet<Area> areas;
    public Area staticWalls;
    public Area playerWalls[];
}
