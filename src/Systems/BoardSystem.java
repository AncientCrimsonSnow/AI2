package Systems;

import Data.*;
import Testing.MyClient;
import Utils.int2;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BoardSystem {
    public static Data.Board InitBoard(Game game, NetworkClient client){
        var board = new Board();
        board.game = game;

        var staticWall = AreaSystem.InitArea(-1);
        var areas = new ArrayList<Area>();

        var indicesMap = new HashMap<Integer, Area>();

        for(var y = 0; y != Board.DEFAULT_BOARD_SIDE_LENGTH; y++){
            for(var x = 0; x != Board.DEFAULT_BOARD_SIDE_LENGTH; x++){

                var areaId = client.getAreaId(x, y);
                var cell = new int2(x,y);

                if(areaId == 0){
                    staticWall.cells.add(cell);
                    continue;
                }

                Area area;
                if(indicesMap.containsKey(areaId)){
                    area = indicesMap.get(areaId);
                }
                else{
                    area = AreaSystem.InitArea(-1);
                    indicesMap.put(areaId, area);
                    areas.add(area);
                }
                area.cells.add(cell);
            }
        }

        var playerWalls = new Area[]{
                AreaSystem.InitArea(0),
                AreaSystem.InitArea(1),
                AreaSystem.InitArea(2)
        };


        board.cellAreaMap = new HashMap<>();
        board.areas = new HashSet<>();
        board.playerWalls = playerWalls;

        for(var i = 0; i != areas.size(); i++){
            var scaledArea = ScaleArea(areas.get(i), Board.SCALE);
            for(var cell : scaledArea.cells){
                board.cellAreaMap.put(cell, scaledArea);
            }
            board.areas.add(scaledArea);
        }

        //Check for doubles
        for(var y = 0; y != Board.BOARD_SIDE_LENGTH; y++){
            for(var x = 0; x != Board.BOARD_SIDE_LENGTH; x++){
                var cell = new int2(x,y);
                var correctArea = board.cellAreaMap.get(cell);
                for(var area : board.areas){
                    if(area != correctArea){
                        area.cells.remove(cell);
                    }
                }
            }
        }

        //Bot Crr Aras
        board.botCrrAreas = new Area[Bot.BOT_COUNT][Player.PLAYER_COUNT];

        for(var p = 0; p != Player.PLAYER_COUNT; p++){
            var playerNumber = (client.getMyPlayerNumber() + p) % 3;
            for(var b = 0; b != Bot.BOT_COUNT; b++){
                board.botCrrAreas[playerNumber][b] = board.cellAreaMap.get(new int2( Math.round(client.getStartX(playerNumber, b) * Board.SCALE), Math.round(client.getStartY(playerNumber, b) * Board.SCALE)));
            }
        }

        //Static Wall Adding
        board.staticWalls = ScaleArea(staticWall, Board.SCALE);
        for(var cell : board.staticWalls.cells){
            var oldArea = board.cellAreaMap.get(cell);
            oldArea.cells.remove(cell);
            board.cellAreaMap.put(cell, board.staticWalls);
        }

        var areasArrCopy = new Area[board.areas.size()];
        board.areas.toArray(areasArrCopy);

        for(var i = 0; i != areasArrCopy.length; i++){
            TrySplitArea(board, areasArrCopy[i]);
        }

        return board;
    }
    public static Data.Board InitBoard(Game game, MyClient client){
        var board = new Board();
        board.game = game;

        var staticWall = AreaSystem.InitArea(-1);
        var areas = new ArrayList<Area>();

        var indicesMap = new HashMap<Integer, Area>();

        for(var y = 0; y != Board.DEFAULT_BOARD_SIDE_LENGTH; y++){
            for(var x = 0; x != Board.DEFAULT_BOARD_SIDE_LENGTH; x++){

                var areaId = client.getAreaId(x, y);
                var cell = new int2(x,y);

                if(areaId == 0){
                    staticWall.cells.add(cell);
                    continue;
                }

                Area area;
                if(indicesMap.containsKey(areaId)){
                    area = indicesMap.get(areaId);
                }
                else{
                    area = AreaSystem.InitArea(-1);
                    indicesMap.put(areaId, area);
                    areas.add(area);
                }
                area.cells.add(cell);
            }
        }

        var playerWalls = new Area[Player.PLAYER_COUNT];

        board.cellAreaMap = new HashMap<>();
        board.areas = new HashSet<>();
        board.playerWalls = playerWalls;

        for(var i = 0; i != areas.size(); i++){
            var scaledArea = ScaleArea(areas.get(i), Board.SCALE);
            for(var cell : scaledArea.cells){
                board.cellAreaMap.put(cell, scaledArea);
            }
            board.areas.add(scaledArea);
        }

        //Check for doubles
        for(var y = 0; y != Board.BOARD_SIDE_LENGTH; y++){
            for(var x = 0; x != Board.BOARD_SIDE_LENGTH; x++){
                var cell = new int2(x,y);
                var correctArea = board.cellAreaMap.get(cell);
                for(var area : board.areas){
                    if(area != correctArea){
                        area.cells.remove(cell);
                    }
                }
            }
        }

        //Bot Crr Aras
        board.botCrrAreas = new Area[Bot.BOT_COUNT][Player.PLAYER_COUNT];

        for(var p = 0; p != Player.PLAYER_COUNT; p++){
            var playerNumber = (client.getMyPlayerNumber() + p) % 3;
            for(var b = 0; b != Bot.BOT_COUNT; b++){
                board.botCrrAreas[playerNumber][b] = board.cellAreaMap.get(new int2( Math.round(client.getStartX(playerNumber, b) * Board.SCALE), Math.round(client.getStartY(playerNumber, b) * Board.SCALE)));
            }
        }

        //Static Wall Adding
        board.staticWalls = ScaleArea(staticWall, Board.SCALE);
        for(var cell : board.staticWalls.cells){
            var oldArea = board.cellAreaMap.get(cell);
            oldArea.cells.remove(cell);
            board.cellAreaMap.put(cell, board.staticWalls);
        }

        var areasArrCopy = new Area[board.areas.size()];
        board.areas.toArray(areasArrCopy);

        for(var i = 0; i != areasArrCopy.length; i++){
            TrySplitArea(board, areasArrCopy[i]);
        }

        return board;
    }

    public static void Update(Board board, Update update){
        //TODO A-Star check for missed updates
        
        var cellToUpdate = new int2(update.x, update.y);
        var oldArea = board.cellAreaMap.get(cellToUpdate);
        RemapCellToArea(board, cellToUpdate, oldArea, board.playerWalls[update.player]);
    }
    private static Area ScaleArea(Area area, float scale){
        var scaledAreaCells = new HashSet<int2>();

        for (var cell : area.cells) {
            var scaledX = Math.round(cell.x * scale);
            var scaledY = Math.round(cell.y * scale);

            if(scaledX == Board.BOARD_SIDE_LENGTH || scaledY == Board.BOARD_SIDE_LENGTH)
                continue;

            var scaledCell = new int2(scaledX, scaledY);
            scaledAreaCells.add(scaledCell);
        }

        return AreaSystem.InitArea(area.occupation, scaledAreaCells);
    }

    private static boolean TrySplitArea(Board board, Area area){
        HashSet<int2> openList = new HashSet<>();
        HashSet<int2> closedList = new HashSet<>();

        var areaIterator = area.cells.iterator();
        openList.add(areaIterator.next());

        while (!openList.isEmpty()) {
            var crrCell = openList.iterator().next();
            openList.remove(crrCell);
            closedList.add(crrCell);

            var cellsToCheck = new int2[]{
                    new int2(crrCell.x - 1, crrCell.y),
                    new int2(crrCell.x + 1, crrCell.y),
                    new int2(crrCell.x, crrCell.y - 1),
                    new int2(crrCell.x, crrCell.y + 1),
            };


            //Add Cells if they are in the same area to the open list
            for (var i = 0; i < cellsToCheck.length; i++) {
                var cellToCheck = cellsToCheck[i];
                if (board.cellAreaMap.containsKey(cellToCheck)) {
                    var neighbourCellArea = board.cellAreaMap.get(cellToCheck);
                    if (neighbourCellArea == area)
                        if (!openList.contains(cellToCheck) && !closedList.contains(cellToCheck))
                            openList.add(cellToCheck);
                }
            }
        }

        //Split if
        if (closedList.size() != area.cells.size()) {
            var area1 = AreaSystem.InitArea(area.occupation);
            var area2 = AreaSystem.InitArea(area.occupation);

            board.areas.remove(area);
            board.areas.add(area1);
            board.areas.add(area2);

            var allCells = new int2[area.cells.size()];
            area.cells.toArray(allCells);

            for(var i = 0; i != allCells.length; i++){
                var cell = allCells[i];
                if (closedList.contains(cell))
                    RemapCellToArea(board, cell, area, area1);
                else
                    RemapCellToArea(board, cell, area, area2);
            }

            for(var p = 0; p != Player.PLAYER_COUNT; p++){
                for(var b = 0; b != Bot.BOT_COUNT; b++){
                    if(board.botCrrAreas[p][b] == area){
                        var botPos = board.game.player[p].bots[b].pos;
                        board.botCrrAreas[p][b] = (area1.cells.contains(botPos))? area1 : area2;
                    }
                }
            }

            return true;
        }
        return false;
    }

    public static void RemapCellToArea(Board board, int2 cell, Area oldArea, Area newArea){
        oldArea.cells.remove(cell);
        newArea.cells.add(cell);

        board.cellAreaMap.put(cell, newArea);
    }

    public static int GetAreaCount(Board board){
        return board.areas.size();
    }
}
