package Discarded;

import Data.Integer2;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static Game Instance;

    public Player[] player = new Player[3];


    private List<Integer2> _playerBotOrder = new ArrayList<>(){{
        add(new Integer2(0,0));
        add(new Integer2(0,1));
        add(new Integer2(0,2));
        add(new Integer2(1,0));
        add(new Integer2(1,1));
        add(new Integer2(1,2));
        add(new Integer2(2,0));
        add(new Integer2(2,1));
        add(new Integer2(2,2));
    }};

    private int _updateIndex = 0;

    private Integer2 _lastUpdatedBot = new Integer2(2,2);

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
        var receivedUpdate = new Integer2(update.player, update.bot);

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
