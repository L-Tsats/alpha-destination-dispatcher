package dispatcher.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import dispatcher.api.dto.PassengerRequest;

class BrainServerTest {

    @Test
    void postedRequestsAreAssignedToCars() throws Exception {
        BrainServer server = new BrainServer(0, 40, 2);   // port 0: any free port
        server.start();
        try {
            BrainApiClient client = new BrainApiClient("http://localhost:" + server.port());

            assertEquals("A", client.submitRequest(new PassengerRequest(3, 9)).elevatorID());
            // car A now holds 3 -> 9, so 5 -> 7 is cheaper on the empty car B
            assertEquals("B", client.submitRequest(new PassengerRequest(5, 7)).elevatorID());
        } finally {
            server.stop();
        }
    }

    @Test
    void elevatorsPullTheirNextDestination() throws Exception {
        BrainServer server = new BrainServer(0, 40, 2);
        server.start();
        try {
            String base = "http://localhost:" + server.port();
            BrainApiClient client = new BrainApiClient(base);
            client.submitRequest(new PassengerRequest(3, 9));   // goes to A

            assertEquals(3, client.getNextDest("A").nextFloor());   // A's first stop is the pickup at 3
            assertEquals(204, statusOf(base + "/api/elevators/B/next-destination"));   // B has nothing
            assertEquals(404, statusOf(base + "/api/elevators/Z/next-destination"));   // no such elevator
            assertEquals(404, statusOf(base + "/api/elevators/A/something-else"));
        } finally {
            server.stop();
        }
    }

    private static int statusOf(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

}
