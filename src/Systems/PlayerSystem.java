package Systems;

import Data.Board;
import Data.Bot;
import Data.Player;
import Testing.MyClient;
import Utils.int2;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import java.util.HashSet;

public class PlayerSystem {
    public static Data.Player InitPlayer(int playerNumber, NetworkClient client){
        var player = new Player();
        player.bots = new Bot[Bot.BOT_COUNT];

        var botStartingPositions = new HashSet<int2>(Bot.BOT_COUNT);
        for(var b = 0; b != Bot.BOT_COUNT; b++){
            var pos = new int2(Math.round(client.getStartX(playerNumber, b) * Board.SCALE), Math.round(client.getStartY(playerNumber, b) * Board.SCALE));
            botStartingPositions.add(pos);
            player.bots[b] = BotSystem.InitBot(pos);
        }

        return player;
    }

    public static Data.Player InitPlayer(int playerNumber, MyClient client){
        var player = new Player();
        player.bots = new Bot[Bot.BOT_COUNT];

        var botStartingPositions = new HashSet<int2>(Bot.BOT_COUNT);
        for(var b = 0; b != Bot.BOT_COUNT; b++){
            var pos = new int2(Math.round(client.getStartX(playerNumber, b) * Board.SCALE), Math.round(client.getStartY(playerNumber, b) * Board.SCALE));
            botStartingPositions.add(pos);
            player.bots[b] = BotSystem.InitBot(pos);
        }

        return player;
    }

    public static boolean Update(Player player, Update update){
        var pos = new int2(update.x, update.y);
        if(pos == player.bots[update.bot].pos)
            return false;

        BotSystem.Update(player.bots[update.bot], pos);
        return true;
    }
}
