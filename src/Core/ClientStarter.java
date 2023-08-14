package Core;

import Data.Board;
import Data.Bot;
import Data.Integer2;
import Data.PathCell;
import Systems.PathSystem;
import Utils.Debug;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import java.util.Queue;

public class ClientStarter {
    //Kreis
    //Viereck
    //Dreieck

    //Rot
    //Grün
    //Blau

    public static void main(String[] args) {
        var networkClient = new NetworkClient(null, "Immense Timewaste", "Will never show up");
        var clientModel = new MainClient(networkClient);

        Debug.Log(networkClient.getMyPlayerNumber());

        Queue<PathCell>[] movesBot = new Queue[Bot.BOT_COUNT];
        var turnPoints = new PathCell[Bot.BOT_COUNT];
        var playerNumber = networkClient.getMyPlayerNumber();

        var updateThread = new Thread(() -> {
            while(networkClient.isAlive()) {
                Update update;
                while ((update = networkClient.pullNextUpdate()) != null) {
                    clientModel.Update(update);
                    var cellToUpdate = new Integer2(update.x, update.y);
                    if(update.player == playerNumber){
                        var turnPoint = turnPoints[update.bot];

                        //TODO bordercross
                        if(turnPoint.pos.equals(cellToUpdate)){
                            networkClient.changeDirection(update.bot, Math.round(turnPoint.value / Board.SCALE));
                            var newDir = PathSystem.GetNewDirection(clientModel.game.player[update.player].bots[update.bot].direction, turnPoint.value < 0);
                            clientModel.game.player[update.player].bots[update.bot].direction = newDir;

                            turnPoints[update.bot] = movesBot[update.bot].remove();
                        }
                    }
                }
            }
        });

        var calcPathThread = new Thread(() -> {
            while(networkClient.isAlive()){
                for(var b = 0; b != Bot.BOT_COUNT; b++){
                    var bot = clientModel.game.player[playerNumber].bots[b];
                    var completePath = PathSystem.GetPath(clientModel.game.board, bot.direction, playerNumber, bot.pos, PathSystem.NEIGHBOUR_FINDERS[b]);
                    var convertedPath = PathSystem.ConvertPath(completePath);

                    movesBot[b] = convertedPath;
                    if(movesBot.length == 0)
                        return;

                    turnPoints[b] = movesBot[b].remove();
                }
            }
        });

        calcPathThread.start();
        updateThread.start();
    }
}
