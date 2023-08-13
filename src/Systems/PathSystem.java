package Systems;

import Data.*;
import Utils.*;

import java.util.*;

public class PathSystem {

    public static final INeighbourFinder[] NEIGHBOUR_FINDERS = {
            new BasicNeighbourFinder(),
            new IgnoreBoardBoundariesNeighbourFinder(),
            new IgnoreOwnWallsNeighbourFinder()
    };

    public static final float NANO_SEC = 1000000000f;
    public static final float PATH_CALCULATION_NANO_TIME = 1f * NANO_SEC;

    public static ArrayList<Integer2> GetPath(Board board, Direction startDirection, int playerNumber, Integer2 startCell, INeighbourFinder neighbourFinder){
        var prePath = new Integer2[]{
                new Integer2(startCell.x, startCell.y),
                ModifyStartCell(startCell, startDirection)
        };

        var openList = new HashSet();
        openList.add(new PathCell(prePath[1], 0));

        var closedList = new HashSet();
        closedList.add(new PathCell(prePath[0], 0));

        var startTime = System.nanoTime();
        while(!openList.isEmpty()){
            var updatedList = OpenListIteration(openList, closedList, board, playerNumber, neighbourFinder);
            if(updatedList.size() == 0)
                break;
            else
                openList = updatedList;
            if(System.nanoTime() >= startTime + PATH_CALCULATION_NANO_TIME)
                break;
        }

        return BuildPath(openList, prePath);
    }

    public static Direction GetNewDirection(Direction prevDirection, boolean leftTurn){
        switch (prevDirection){
            case Bot -> {
                return leftTurn? Direction.Right : Direction.Left;
            }
            case Top -> {
                return leftTurn? Direction.Left : Direction.Right;
            }
            case Left -> {
                return leftTurn? Direction.Bot : Direction.Top;
            }
            case Right ->{
                return leftTurn? Direction.Top : Direction.Bot;
            }
            default -> {
                Debug.Log("??????????");
                return Direction.Left;
            }
        }
    }

    private static Integer2 ModifyStartCell(Integer2 startCell, Direction startDirection){
        switch (startDirection){
            case Bot -> {
                return new Integer2(startCell.x, startCell.y + 1);
            }
            case Top -> {
                return new Integer2(startCell.x, startCell.y - 1);
            }
            case Left -> {
                return new Integer2(startCell.x - 1, startCell.y);
            }
            case Right ->{
                return new Integer2(startCell.x + 1, startCell.y);
            }
            default -> {
                return startCell;
            }
        }
    }

    private static ArrayList<Integer2> BuildPath(HashSet<PathCell> openList, Integer2[] prePath){
        var crrCell = Collections.max(openList);
        var result = new ArrayList<Integer2>();
        while(crrCell.cameFrom != null){
            result.add(crrCell.pos);
            crrCell = crrCell.cameFrom;
        }
        Collections.reverse(result);
        result.add(0, prePath[0]);
        result.add(1, prePath[1]);
        return result;
    }

    public static HashSet<PathCell> OpenListIteration(HashSet<PathCell> openList, HashSet<PathCell> closedList, Board board, int playerNumber, INeighbourFinder neighbourFinder){
        var openListCopy = new HashSet<PathCell>();
        for(var pathCell : openList)
            openListCopy.add(pathCell.clone());

        var result = new HashSet<PathCell>();

        while(!openListCopy.isEmpty()){
            var crrCell = openListCopy.iterator().next();
            openListCopy.remove(crrCell);
            closedList.add(crrCell);

            var cellsToCheck = neighbourFinder.GetNeighbours(board, playerNumber, crrCell.pos);

            for(var i = 0; i < cellsToCheck.size(); i++){
                var cellToCheck = new PathCell(cellsToCheck.get(i), 0);

                if(result.contains(cellToCheck)){
                    var value = CalcValue(crrCell, cellToCheck, board, playerNumber);
                    if(value > cellToCheck.value){
                        cellToCheck.value = value;
                        cellToCheck.cameFrom = crrCell;
                        result.remove(cellToCheck);
                        result.add(cellToCheck);
                    }
                }
                else if(!(closedList.contains(cellToCheck) || openListCopy.contains(cellToCheck))){
                    cellToCheck.value = CalcValue(crrCell, cellToCheck, board, playerNumber);
                    cellToCheck.cameFrom = crrCell;
                    result.add(cellToCheck);
                }
            }
        }
        return result;
    }

    private static int CalcValue(PathCell crrCell, PathCell cellToCalc, Board board, int playerNumber){
        var result = crrCell.value;
        var prevCell = crrCell.cameFrom;
        var crrArea = board.cellAreaMap.get(crrCell.pos);
        var cellToCalcArea = board.cellAreaMap.get(cellToCalc.pos);

        //wenn er auf ein neues gebiet kommt, was einem noch nicht gehört
        if(crrArea != cellToCalcArea){
            if(cellToCalcArea.occupation != playerNumber){
                result += Math.round(cellToCalcArea.cells.size() / 2f);
            }
        }

        if(prevCell == null)
            return result;

        //true: vertical, false: horizontal;
        var previousAxisDirection = crrCell.pos.x == crrCell.cameFrom.pos.x;
        var currentAxisDirection = cellToCalc.pos.x == crrCell.pos.x;

        if(previousAxisDirection == currentAxisDirection)
            result += Math.round(25 * Board.SCALE);

        return result;
    }

    public static Queue<PathCell> ConvertPath(ArrayList<Integer2> path){
        //TODO WRONG
        Queue<PathCell> result = new LinkedList<>();

        for(var i = 2; i != path.size(); i++){
            var current = path.get(i);
            var previous = path.get(i-1);
            var previous2 = path.get(i-2);

            //true: vertical, false: horizontal;
            var previousAxisDirection = previous.x == previous2.x;
            var currentAxisDirection = current.x == previous.x;

            if(previousAxisDirection != currentAxisDirection){

                int coord;
                boolean localSpaceLeft;

                //vertical
                if(previousAxisDirection){
                    var worldSpaceLeft = current.x < previous.x;
                    localSpaceLeft = (previous2. y > previous.y)? worldSpaceLeft : !worldSpaceLeft;
                    coord = previous.y;
                }
                //horizontal
                else{
                    var worldSpaceUp = current.y < previous.y;
                    localSpaceLeft = (previous2.x < previous.x)? worldSpaceUp : !worldSpaceUp;
                    coord = previous.x;

                }
                if(localSpaceLeft)
                    coord *= -1;

                result.add(new PathCell(previous, coord));
            }
        }
        return result;
    }

    public static void UpdateDirection(Bot bot, PathCell changedDirection){
        switch (bot.direction){
            case Bot -> {
                if(changedDirection.value < 0)
                    bot.direction = Direction.Left;
                else
                    bot.direction = Direction.Right;
            }
            case Top -> {
                if(changedDirection.value < 0)
                    bot.direction = Direction.Right;
                else
                    bot.direction = Direction.Left;
            }
            case Left -> {
                if(changedDirection.value < 0)
                    bot.direction = Direction.Bot;
                else
                    bot.direction = Direction.Top;
            }
            case Right ->{
                if(changedDirection.value < 0)
                    bot.direction = Direction.Top;
                else
                    bot.direction = Direction.Bot;
            }
        }
    }
}
