package Systems;

import Data.Bot;
import Data.Direction;
import Data.Integer2;

public class BotSystem {
    public static Bot InitBot(Integer2 pos){
        var bot = new Bot();
        bot.pos = pos;
        bot.alive = true;
        bot.direction = Direction.Right;

        return bot;
    }

    public static void Update(Bot bot, Integer2 pos){
        bot.pos = pos;
    }
}
