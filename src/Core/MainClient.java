package Core;

import Systems.GameSystem;
import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

import static Testing.Tester.SmallerEqualTest;

public class MainClient {
    Data.Game game;

    public MainClient(NetworkClient client) {
        this.game = GameSystem.InitGame(client);
    }

    public void Update(Update update){
        var startTime = System.nanoTime();
        GameSystem.Update(game, update);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        SmallerEqualTest(executionTime/1000000000f, 0.003f, "UpdateDurationTest");
    }
}
