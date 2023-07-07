package Core;

import Utils.Debug;
import Utils.int2;

public class Bot {
    public int2 pos;

    private int _playerNumber;
    public Area crrArea;

    public boolean alive = true;

    public Bot(int2 pos, int playerNumber) {
        this.pos = pos;

        _playerNumber = playerNumber;
        crrArea = Board.Instance.cellAreaMap.get(pos);
    }

    public void Update(int2 pos){
        this.pos = pos;
        var newArea = Board.Instance.cellAreaMap.get(pos);

        if(crrArea.cells.size() == 0)
            Debug.Log("TEST");

        if(crrArea != newArea){
            newArea.occupation = _playerNumber;
            Board.Instance.TrySplitArea(crrArea);
        }

        crrArea = newArea;
    }
}
