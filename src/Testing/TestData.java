package Testing;

import Data.Board;
import Utils.int2;

import java.util.HashMap;
import java.util.HashSet;

public class TestData {

    private static final float AREA_SIDE_LENGTH = Math.round(Board.DEFAULT_BOARD_SIDE_LENGTH/3f);
    public static final int2[][] TEST_BOT_POS = {
            {new int2(0, Math.round(AREA_SIDE_LENGTH * .5f)), new int2(0,Math.round(AREA_SIDE_LENGTH * 1.5f)), new int2(0,Math.round(AREA_SIDE_LENGTH * 2.5f))},
            {new int2(Math.round(AREA_SIDE_LENGTH/3f),0), new int2(Math.round(AREA_SIDE_LENGTH + Math.round(AREA_SIDE_LENGTH * 2f / 3f)),0),new int2(Math.round(2 * AREA_SIDE_LENGTH + Math.round(AREA_SIDE_LENGTH / 3f)),0)},
            {new int2(Math.round(AREA_SIDE_LENGTH * 2f / 3f), Core.Board.DEFAULT_BOARD_SIDE_LENGTH - 1), new int2(Math.round(AREA_SIDE_LENGTH + Math.round(AREA_SIDE_LENGTH / 3f)), Core.Board.DEFAULT_BOARD_SIDE_LENGTH - 1), new int2(Core.Board.DEFAULT_BOARD_SIDE_LENGTH - Math.round(AREA_SIDE_LENGTH / 3f), Core.Board.DEFAULT_BOARD_SIDE_LENGTH - 1)}
    };

    public static HashMap<int2, Integer> GetAreaIdMap(){
        var staticWallCells = CreateStaticWallCells();

        var result = new HashMap<int2, Integer>();
        for(var y = 0; y != Board.DEFAULT_BOARD_SIDE_LENGTH; y++)
            for(var x = 0; x != Board.DEFAULT_BOARD_SIDE_LENGTH; x++)
                PutIn(x, y, result, staticWallCells);

        return result;
    }
    public static HashSet<int2> CreateStaticWallCells(){
        var staticWallCellS = new HashSet<int2>();

        for(var x = 0; x <= AREA_SIDE_LENGTH * 1.5f; x++)
            staticWallCellS.add(new int2(x, Math.round(AREA_SIDE_LENGTH * (2f + 4f/5f))));

        return staticWallCellS;
    }

    private static void PutIn(int x, int y, HashMap<int2, Integer> result, HashSet<int2> staticWallCells){
        //find Row
        if(y < AREA_SIDE_LENGTH)
            PutInRow(0, x, y, result, staticWallCells);
        else if(y < AREA_SIDE_LENGTH * 2f)
            PutInRow(1, x, y, result, staticWallCells);
        else
            PutInRow(2, x, y, result, staticWallCells);
    }

    private static void PutInRow(int row, int x, int y, HashMap<int2, Integer> result, HashSet<int2> staticWallCells){
        var cell = new int2(x,y);
        if(x < AREA_SIDE_LENGTH)
            result.put(cell, staticWallCells.contains(cell)? 0 : row * 3 + 1);
        else if(x < AREA_SIDE_LENGTH * 2f)
            result.put(cell, staticWallCells.contains(cell)? 0 : row * 3 + 2);
        else
            result.put(cell, staticWallCells.contains(cell)? 0 : row * 3 + 3);
    }
}
