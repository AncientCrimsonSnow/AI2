package Discarded;

import Data.Integer2;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

public class Player {
    public int playerNumber;
    public Area walls = new Area();
    public Bot[] bots = new Bot[3];

    public Player(int playerNumber, NetworkClient client) {
        this.playerNumber = playerNumber;
        Board.Instance.areas.add(walls);

        for(var i = 0; i != bots.length; i++){
            var pos = new Integer2(client.getStartX(playerNumber, i), client.getStartY(playerNumber, i));
            bots[i] = new Bot(pos, playerNumber);
            walls.cells.add(pos);
            Board.Instance.RemapCellToArea(pos, walls);
        }
        walls.occupation = playerNumber;
    }

    public Player(int playerNumber){
        this.playerNumber = playerNumber;
        walls.occupation = playerNumber;
    }

    public void UpdateBot(Update update){
        var pos = new Integer2(update.x, update.y);
        if(pos == bots[update.bot].pos)
            return;

        bots[update.bot].Update(pos);
        walls.cells.add(pos);

        Board.Instance.RemapCellToArea(pos, walls);
    }
}
