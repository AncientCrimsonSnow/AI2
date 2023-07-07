package Core;

import Utils.int2;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static Game Instance;

    public Player[] player = new Player[3];


    private List<int2> _playerBotOrder = new ArrayList<>(){{
        add(new int2(0,0));
        add(new int2(0,1));
        add(new int2(0,2));
        add(new int2(1,0));
        add(new int2(1,1));
        add(new int2(1,2));
        add(new int2(2,0));
        add(new int2(2,1));
        add(new int2(2,2));
    }};

    private int _updateIndex = 0;

    private int2 _lastUpdatedBot = new int2(2,2);

    public Game(NetworkClient client) {
        Instance = this;
        new Board(client);

        for(var i = 0; i != player.length; i++){
            player[i] = new Player(client.getMyPlayerNumber() + i % 3, client);
        }

        Board.Instance.TrySplitAllAreas();
    }

    public Game(){
        Instance = this;
        new Board();
    }

    public void Update(Update update){
        var expectedUpdate = _playerBotOrder.get(_updateIndex);
        var receivedUpdate = new int2(update.player, update.bot);

        while(!expectedUpdate.equals(receivedUpdate)){
            _playerBotOrder.remove(_updateIndex);
            _updateIndex %= _playerBotOrder.size();
            player[expectedUpdate.x].bots[expectedUpdate.y].alive = false;
            expectedUpdate = _playerBotOrder.get(_updateIndex);
        }

        update.x *= Board.SCALE;
        update.y *= Board.SCALE;

        for(var i = 0; i != player.length; i++){
            if(player[i].playerNumber == update.player){
                player[i].UpdateBot(update);
                break;
            }
        }
        _updateIndex = (_updateIndex + 1) % _playerBotOrder.size();
    }
}
