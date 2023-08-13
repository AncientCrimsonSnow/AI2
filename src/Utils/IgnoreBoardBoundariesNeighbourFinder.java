package Utils;

import Data.Board;
import Data.Player;
import Data.Integer2;

import java.util.ArrayList;

public class IgnoreBoardBoundariesNeighbourFinder implements INeighbourFinder {
    @Override
    public ArrayList<Integer2> GetNeighbours(Board board, int playerNumber, Integer2 center) {
        var result = new ArrayList<Integer2>();

        var cellsToCheck = new Integer2[]{
                new Integer2(center.x - 1, center.y),
                new Integer2(center.x + 1, center.y),
                new Integer2(center.x, center.y - 1),
                new Integer2(center.x, center.y + 1),
        };

        for (var i = 0; i < cellsToCheck.length; i++) {
            var cellToCheck = cellsToCheck[i];

            if(cellToCheck.x >= Board.BOARD_SIDE_LENGTH)
                cellToCheck.x %= Board.BOARD_SIDE_LENGTH;
            else if(cellToCheck.x < 0)
                cellToCheck.x += Board.BOARD_SIDE_LENGTH;

            if(cellToCheck.y >= Board.BOARD_SIDE_LENGTH)
                cellToCheck.y %= Board.BOARD_SIDE_LENGTH;
            else if(cellToCheck.y < 0)
                cellToCheck.y += Board.BOARD_SIDE_LENGTH;

            var neighbourCellArea = board.cellAreaMap.get(cellToCheck);
            if (neighbourCellArea != board.staticWalls){
                var isPlayerWall = false;
                for(var p = 0; p != Player.PLAYER_COUNT; p++){
                    if(neighbourCellArea == board.playerWalls[p])
                        isPlayerWall = true;
                }

                if(!isPlayerWall){
                    result.add(cellToCheck);
                }
            }
        }
        return result;
    }
}
