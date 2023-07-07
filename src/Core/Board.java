package Core;

import Utils.int2;
import lenz.htw.duktus.net.NetworkClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Board {

    public static final int ScaleFactor = 3;
    public static final int DEFAULT_BOARD_SIDE_LENGTH = 256;
    public static final float SCALE = (float) (1/Math.pow(2, ScaleFactor));
    public static final int BOARD_SIDE_LENGTH = (int) (DEFAULT_BOARD_SIDE_LENGTH * SCALE);

    public static Board Instance;
    public Map<int2, Area> cellAreaMap = new HashMap<>();
    public ArrayList<Area> areas = new ArrayList();

    public Area staticWalls;

    public Board(NetworkClient client) {
        Instance = this;

        var indicesMap = new HashMap<Integer, Area>();

        for(var y = 0; y != BOARD_SIDE_LENGTH; y++){
            for(var x = 0; x != BOARD_SIDE_LENGTH; x++){
                var areaId = client.getAreaId((int) (x/SCALE), (int) (y/SCALE));
                Area area;
                if(indicesMap.containsKey(areaId)){
                    area = indicesMap.get(areaId);
                }
                else{
                    areas.add(area = new Area());
                    indicesMap.put(areaId, area);
                }
                var cell = new int2(x,y);
                area.cells.add(cell);
                cellAreaMap.put(cell, area);
            }
        }
        staticWalls = indicesMap.get(0);
    }

    public Board(){
        Instance = this;
    }

    public int GetAreaCount(){
        if(Game.Instance.player[0] == null)
            return areas.size();

        var resultWithoutWalls = new ArrayList<Area>();
        for(var i = 0; i != areas.size(); i++){
            var area = areas.get(i);
            if(
                area != staticWalls                     &&
                area != Game.Instance.player[0].walls   &&
                area != Game.Instance.player[1].walls   &&
                area != Game.Instance.player[2].walls)
                resultWithoutWalls.add(area);
        }
        return resultWithoutWalls.size();
    }

    public void TrySplitAllAreas(){
        for(var i = 0; i != areas.size(); i++){
            TrySplitArea(areas.get(i));
        }
    }
    public void TrySplitArea(Area area){
        HashSet<int2> openList = new HashSet<>();
        HashSet<int2> closedList = new HashSet<>();

        var areaIterator = area.cells.iterator();
        openList.add(areaIterator.next());

        while (!openList.isEmpty()) {
            int2 crrCell = openList.iterator().next();
            openList.remove(crrCell);
            closedList.add(crrCell);

            ArrayList<int2> neighbourCells = new ArrayList<>();

            int2[] cellToCheck = new int2[]{
                    new int2(crrCell.x - 1, crrCell.y),
                    new int2(crrCell.x + 1, crrCell.y),
                    new int2(crrCell.x, crrCell.y - 1),
                    new int2(crrCell.x, crrCell.y + 1),
            };

            for (int i = 0; i < cellToCheck.length; i++) {
                if (cellAreaMap.containsKey(cellToCheck[i])) {
                    Area neighbourCellArea = cellAreaMap.get(cellToCheck[i]);
                    if (neighbourCellArea == area)
                        neighbourCells.add(cellToCheck[i]);
                }
            }

            for (int i = 0; i < neighbourCells.size(); i++) {
                int2 neighbourCell = neighbourCells.get(i);
                if (!openList.contains(neighbourCell) && !closedList.contains(neighbourCell)) {
                    openList.add(neighbourCell);
                }
            }
        }

        if (closedList.size() != area.cells.size()) {
            Area area1 = new Area();

            areaIterator = area.cells.iterator();
            while(areaIterator.hasNext()) {
                var cell = areaIterator.next();
                if (!closedList.contains(cell)) {
                    cellAreaMap.replace(cell, area1);
                    area1.cells.add(cell);
                }
            }

            Area area0 = new Area();

            for (int2 closedCell : closedList) {
                cellAreaMap.replace(closedCell, area0);
                area0.cells.add(closedCell);
            }

            for (int p = 0; p != Game.Instance.player.length; p++) {
                for (int i = 0; i != Game.Instance.player[p].bots.length; i++) {
                    int2 botPos = Game.Instance.player[p].bots[i].pos;
                    Area crrAreaOfBot = cellAreaMap.get(botPos);
                    Game.Instance.player[p].bots[i].crrArea = crrAreaOfBot;
                }
            }
            areas.remove(area);
            areas.add(area0);
            areas.add(area1);
        }
    }

    public void RemapCellToArea(int2 cell, Area area){
        cellAreaMap.get(cell).cells.remove(cell);
        cellAreaMap.replace(cell, area);
    }
}
