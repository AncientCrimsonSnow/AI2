package Testing;

import Data.Integer2;

import java.util.HashMap;

public class MyClient {

    private HashMap<Integer2, Integer> _areaIdMap;

    public MyClient() {
        _areaIdMap = TestData.GetAreaIdMap();
    }

    public int getStartX(int player, int bot){
        return TestData.TEST_BOT_POS[player][bot].x;
    }

    public int getStartY(int player, int bot){
        return TestData.TEST_BOT_POS[player][bot].y;
    }

    public int getAreaId(int x, int y){
        return _areaIdMap.get(new Integer2(x,y));
    }

    public int getMyPlayerNumber(){
        return 0;
    }


}
