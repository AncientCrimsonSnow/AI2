package Core;

import lenz.htw.duktus.net.NetworkClient;
import lenz.htw.duktus.net.Update;

public class ClientStarter {
    public static void main(String[] args) {
        var networkClient = new NetworkClient(null, "Scout Regiment", "Freedom or Death!");
        var clientModel = new MainClient(networkClient);

        while(networkClient.isAlive()){
            Update update;
            while((update = networkClient.pullNextUpdate()) != null){
                clientModel.Update(update);
            }
            // x < 0 Links x > 0 Rechts
        }
    }
}
