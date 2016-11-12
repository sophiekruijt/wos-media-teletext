package nl.wos.teletext.modules;

import nl.wos.teletext.components.PhecapConnector;
import nl.wos.teletext.components.SportModule;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.SportPouleDao;
import nl.wos.teletext.mockserver.PhetxtMockServer;
import nl.wos.teletext.models.SportPoule;
import nl.wos.teletext.util.TextClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SportModuleTest {
    PhetxtMockServer mockServer = new PhetxtMockServer();
    private TextClient textClient = new TextClient();

    @Mock private SportPouleDao sportPouleDao;
    @Spy private PhecapConnector phecapConnector;

    @Spy private SportModule sportModule;

    @Before
    public void setUp() throws SQLException, IOException, URISyntaxException {
        sportModule.setSportPouleDao(sportPouleDao);
        sportModule.setTeletextConnector(phecapConnector);

        Map<String, SportPoule> sportPoules = new HashMap<>();
        sportPoules.put("Handbal Dames 2e divisie B", new SportPoule("Handbal Dames 2e divisie B", "Handbal Dames 2e divisie"));
        sportPoules.put("Voetbal 2016/2017 Zondag 2e klasse C", new SportPoule("Voetbal 2016/2017 Zondag 2e klasse C", "Voetbal Zondag 2e klasse C"));

        when(sportModule.getSportPoules()).thenReturn(sportPoules);

        URL u = getClass().getResource("/sport-scores-testdata.xml");
        String mockData = String.join("", Files.readAllLines(Paths.get(u.toURI()), StandardCharsets.UTF_8));
        when(sportModule.getSportData()).thenReturn(mockData);
    }

    @Test
    public void doTeletextUpdateTest() throws Exception {
        sportModule.doTeletextUpdate();

        Thread.sleep(2000);
        assertThat(textClient.getTeletextLine(610, 0, 0),  is("Handbal Dames 2e divisie........... 611"));
        assertThat(textClient.getTeletextLine(610, 0, 1),  is("Voetbal Zondag 2e klasse C......... 612"));
        assertThat(textClient.getTeletextLine(611, 1, 2),  is("Programma 12 november"));
        assertThat(textClient.getTeletextLine(611, 1, 10), is("Programma 13 november"));
        assertThat(textClient.getTeletextLine(611, 1, 11), is(""));
        assertThat(textClient.getTeletextLine(611, 1, 12), is("VHC                  - VOC 2              "));
        assertThat(textClient.getTeletextLine(611, 2, 2),  is("            GS  GW  GL  VL  PT   V   T  "));
        assertThat(textClient.getTeletextLine(611, 2, 3),  is("   Westsite 5   4   0   1   8   133 113     "));
        assertThat(textClient.getTeletextLine(611, 2, 4),  is("        BFC 5   4   0   1   8   134 116     "));
        assertThat(textClient.getTeletextLine(611, 2, 5),  is("     Dongen 5   3   1   1   7   124 115     "));
    }
}