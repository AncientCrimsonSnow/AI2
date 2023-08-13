package Discarded;

import Data.Integer2;

public class Bot {
    public Integer2 pos;

    private int _playerNumber;
    public Area crrArea;

    public boolean alive = true;

    public Bot(Integer2 pos, int playerNumber) {
        this.pos = pos;

        _playerNumber = playerNumber;
        crrArea = Board.Instance.cellAreaMap.get(pos);
    }

    public void Update(Integer2 pos){
        this.pos = pos;
        var newArea = Board.Instance.cellAreaMap.get(pos);

        if(crrArea != newArea){
            newArea.occupation = _playerNumber;
            Board.Instance.TrySplitArea(crrArea);
        }

        crrArea = newArea;
    }
}
