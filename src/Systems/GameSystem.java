package Systems;

import Core.Board;
import Data.Game;
import Data.Player;
import Testing.MyClient;
import Utils.int2;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import java.util.ArrayList;

public class GameSystem {
    public static Data.Game InitGame(NetworkClient client){
        var game = new Data.Game();

        game.board = BoardSystem.InitBoard(game, client);

        game.player = new Player[Player.PLAYER_COUNT];
        for(var p = 0; p != Player.PLAYER_COUNT; p++){
            var playerNumber = (client.getMyPlayerNumber() + p) % 3;
            game.player[playerNumber] = PlayerSystem.InitPlayer(playerNumber, client);
        }

        game.updateIndex = 0;
        game.playerBotOrder = new ArrayList<>(){{
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

        return game;
    }

    public static Data.Game InitGame(MyClient client){
        var game = new Data.Game();

        game.player = new Player[Player.PLAYER_COUNT];
        for(var p = 0; p != Player.PLAYER_COUNT; p++){
            var playerNumber = (client.getMyPlayerNumber() + p) % 3;
            game.player[playerNumber] = PlayerSystem.InitPlayer(playerNumber, client);
        }

        game.board = BoardSystem.InitBoard(game, client);

        game.updateIndex = 0;
        game.playerBotOrder = new ArrayList<>(){{
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

        return game;
    }

    public static void UpdateGame(Game game, Update update){
        var expectedUpdate = game.playerBotOrder.get(game.updateIndex);
        var receivedUpdate = new int2(update.player, update.bot);

        while(!expectedUpdate.equals(receivedUpdate)){
            game.playerBotOrder.remove(game.updateIndex);
            game.updateIndex %= game.playerBotOrder.size();
            game.player[expectedUpdate.x].bots[expectedUpdate.y].alive = false;
            expectedUpdate = game.playerBotOrder.get(game.updateIndex);
        }
        update.x *= Board.SCALE;
        update.y *= Board.SCALE;

        if(PlayerSystem.Update(game.player[update.player], update))
            BoardSystem.Update(game.board, update);

        game.updateIndex = ( game.updateIndex + 1) % game.playerBotOrder.size();
    }
}
