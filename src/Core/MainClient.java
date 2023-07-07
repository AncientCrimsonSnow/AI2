package Core;

import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

public class MainClient {
    Game game;

    public MainClient(NetworkClient client) {
        this.game = new Game(client);
    }

    public void Update(Update update){
        game.Update(update);
    }
}
