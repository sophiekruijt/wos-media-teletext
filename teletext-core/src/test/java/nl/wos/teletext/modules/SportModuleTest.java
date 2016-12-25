package nl.wos.teletext.modules;

import nl.wos.teletext.components.PhecapConnector;
import nl.wos.teletext.components.SportModule;
import nl.wos.teletext.mockserver.PhetxtMockServer;
import nl.wos.teletext.util.TextClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SportModuleTest {
    PhetxtMockServer mockServer = new PhetxtMockServer();
    private TextClient textClient = new TextClient();

    @Spy private PhecapConnector phecapConnector;

    @Spy private SportModule sportModule;

    @Before
    public void setUp() throws Exception {
        sportModule.setTeletextConnector(phecapConnector);

        String mockData = String.join("", Files.readAllLines(
                Paths.get(getClass().getResource("/sport-scores-testdata.xml").toURI()), StandardCharsets.UTF_8));
        when(sportModule.getSportData()).thenReturn(mockData);

        String sportPouleMockData = String.join("", Files.readAllLines(
                Paths.get(getClass().getResource("/sport-poules-testdata.html").toURI()), StandardCharsets.UTF_8));
        when(sportModule.getSportPouleData()).thenReturn(sportPouleMockData);
    }

    @Test
    public void doTeletextUpdateTest() throws Exception {
        sportModule.doTeletextUpdate();

        Thread.sleep(2000);
        assertThat(textClient.getTeletextLine(610, 1, 0),  is("Badminton Competitie Eredivisie.... 611"));
        assertThat(textClient.getTeletextLine(610, 1, 1),  is("Basketball Dames Promotiedivisie... 612"));
        assertThat(textClient.getTeletextLine(615, 1, 2),  is("Programma 12 november"));
        assertThat(textClient.getTeletextLine(615, 1, 4),  is("VOC Amsterdam        - Kwiek              "));
        assertThat(textClient.getTeletextLine(618, 1, 2),  is("Programma 12 november"));
        assertThat(textClient.getTeletextLine(618, 1, 9),  is("Programma 13 november"));
        assertThat(textClient.getTeletextLine(618, 1, 12), is("HVV'70               - Rapiditas          "));
        assertThat(textClient.getTeletextLine(618, 2, 3),  is("     Gemini 5   4   0   1   8   138 132     "));
        assertThat(textClient.getTeletextLine(618, 2, 14), is(" Westlandia 5   1   0   4   2   128 152     "));
    }

    @Test
    public void parseSportpoules () throws Exception {
        List<String> sportPoules = sportModule.getSportPoules();
        assertThat(sportPoules.size(), is(38));
        assertThat(sportPoules.get(0), is("Badminton Competitie Eredivisie"));
        assertThat(sportPoules.get(1), is("Badminton Competitie Eredivisie/Kampioenspoule"));
        assertThat(sportPoules.get(2), is("Basketball Dames Promotiedivisie"));
        assertThat(sportPoules.get(25), is("Voetbal 2016/2017 Zaterdag 3e klasse B"));
        assertThat(sportPoules.get(30), is("Voetbal 2016/2017 Jeugd O-17 2e divisie B"));
    }
}