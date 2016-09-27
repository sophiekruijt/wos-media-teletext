package nl.wos.teletext.steps;

import cucumber.api.java8.En;
import nl.wos.teletext.mockserver.PhetxtMockServer;

public class MockServerSteps implements En {
    public MockServerSteps() {
        Given("^a started mockserver on port (\\d+)$", (Integer port) -> {
            PhetxtMockServer mockServer = new PhetxtMockServer(port);
        });
    }
}
