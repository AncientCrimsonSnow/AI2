package Systems;

import Data.Board;
import Data.Game;
import Data.Integer2;
import Data.Player;
import Testing.MyClient;
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

        return game;
    }

    public static void Update(Game game, Update update){
        var expectedUpdate = game.playerBotOrder.get(game.updateIndex);
        var receivedUpdate = new Integer2(update.player, update.bot);

        while(!expectedUpdate.equals(receivedUpdate)){
            game.player[expectedUpdate.x].bots[expectedUpdate.y].alive = false;
            game.playerBotOrder.remove(game.updateIndex);
            game.updateIndex %= game.playerBotOrder.size();

            var botDeathCell = game.player[expectedUpdate.x].bots[expectedUpdate.y].pos;
            var botDeathArea = game.board.cellAreaMap.get(botDeathCell);
            BoardSystem.RemapCellToArea(game.board, botDeathCell, botDeathArea, game.board.playerWalls[update.player]);
            BoardSystem.TrySplitArea(game.board, botDeathArea);

            expectedUpdate = game.playerBotOrder.get(game.updateIndex);
        }

        update.x = Math.round(update.x * Board.SCALE);
        update.y = Math.round(update.y * Board.SCALE);

        if(update.x == Data.Board.BOARD_SIDE_LENGTH)
            update.x--;
        if(update.y == Data.Board.BOARD_SIDE_LENGTH)
            update.y--;

        var oldBotPos = game.player[update.player].bots[update.bot].pos;
        if(PlayerSystem.Update(game.player[update.player], update))
            BoardSystem.Update(game.board, update, oldBotPos);

        game.updateIndex = (game.updateIndex + 1) % game.playerBotOrder.size();
    }
}
