package dispatcher.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        server.setPollTimeoutMs(200);   // B has nothing: short wait, then 204
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

    @Test
    void waitingElevatorIsWokenByANewRequest() throws Exception {
        BrainServer server = new BrainServer(0, 40, 1);
        server.setPollTimeoutMs(10_000);
        server.start();
        try {
            BrainApiClient client = new BrainApiClient("http://localhost:" + server.port());

            // The elevator asks first, with nothing to do, so its request is held open
            CompletableFuture<Integer> pulled = CompletableFuture.supplyAsync(() -> {
                try {
                    return client.getNextDest("A").nextFloor();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(300);
            assertFalse(pulled.isDone());                     // still waiting

            long t0 = System.nanoTime();
            client.submitRequest(new PassengerRequest(3, 9));
            int floor = pulled.get(5, TimeUnit.SECONDS);
            double wokeAfterMs = (System.nanoTime() - t0) / 1e6;

            assertEquals(3, floor);
            assertTrue(wokeAfterMs < 2_000, "woke after " + wokeAfterMs + " ms, long before the 10 s timeout");
        } finally {
            server.stop();
        }
    }

    @Test
    void longPollTimesOutWith204WhenNothingArrives() throws Exception {
        BrainServer server = new BrainServer(0, 40, 1);
        server.setPollTimeoutMs(500);
        server.start();
        try {
            long t0 = System.nanoTime();
            int status = statusOf("http://localhost:" + server.port() + "/api/elevators/A/next-destination");
            double tookMs = (System.nanoTime() - t0) / 1e6;

            assertEquals(204, status);
            assertTrue(tookMs >= 450, "answered after " + tookMs + " ms, should have waited about 500 ms");
        } finally {
            server.stop();
        }
    }

    private static int statusOf(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

}
