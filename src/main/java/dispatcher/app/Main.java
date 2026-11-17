
package dispatcher.app;

import java.io.IOException;

import dispatcher.api.BrainServer;

public class Main {
    // Takes in args [floors, elevators]
    public static void main(String[] args) throws IOException {
        int floors = args.length >= 2 ? Integer.parseInt(args[0]) : 40;
        int elevators = args.length >= 2 ? Integer.parseInt(args[1]) : 4;

        BrainServer httpServer = new BrainServer(8080, floors, elevators);
        httpServer.start();
        System.out.println("Brain running on port 8080 (" + floors + " floors, " + elevators + " elevators)");
    }
    
}
