package Systems;

import Data.Bot;
import Utils.int2;

public class BotSystem {
    public static Bot InitBot(int2 pos){
        var bot = new Bot();
        bot.pos = pos;
        bot.alive = true;

        return bot;
    }

    public static void Update(Bot bot, int2 pos){
        bot.pos = pos;
    }
}
