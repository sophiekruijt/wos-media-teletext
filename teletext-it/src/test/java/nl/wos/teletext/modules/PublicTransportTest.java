package nl.wos.teletext.modules;

import nl.wos.teletext.dao.TrainStationDao;
import nl.wos.teletext.ejb.PhecapConnector;
import nl.wos.teletext.ejb.PublicTransportModule;
import nl.wos.teletext.entity.TrainStation;
import nl.wos.teletext.util.TextClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublicTransportTest {

    TextClient textClient = new TextClient();

    @Mock
    TrainStationDao trainStationDao;

    @Spy
    PhecapConnector phecapConnector;

    @Spy
    PublicTransportModule publicTransportModule;

    @Before
    public void setUp() throws SQLException, IOException, URISyntaxException {
        publicTransportModule.setTeletextConnector(phecapConnector);
        publicTransportModule.setTrainStationDao(trainStationDao);

        List<TrainStation> trainstationMockList= new ArrayList();
        trainstationMockList.add(initializeMockData("mss", "Maassluis", "701"));
        trainstationMockList.add(initializeMockData("msw", "Maassluis West", "702"));
        trainstationMockList.add(initializeMockData("rtd", "Rotterdam", "703"));
        trainstationMockList.add(initializeMockData("abc", "Not existing station", "704"));
        when(publicTransportModule.getTrainStations()).thenReturn(trainstationMockList);
    }

    @Test
    public void DoTeletextUpdateTest() throws Exception {
        publicTransportModule.doTeletextUpdate();

        verify(publicTransportModule, times(1)).doTeletextUpdate();
        verify(phecapConnector, times(1)).uploadFilesToTeletextServer(any());
        Thread.sleep(1000);
        assertThat(textClient.getTeletextLine(701, 0, 0), is("\u0002MAASSLUIS"));
        assertThat(textClient.getTeletextLine(701, 0, 3), is(" 01:52      \u0007Hoek van Holland Haven   \u00033"));
        assertThat(textClient.getTeletextLine(702, 0, 3), is(" 01:55      \u0007Hoek van Holland Haven   \u00031"));
        assertThat(textClient.getTeletextLine(702, 0, 4), is(" 02:03      \u0007Rotterdam Centraal       \u00032"));
        assertThat(textClient.getTeletextLine(703, 0, 4), is(" 23:47      \u0007Den Haag Centraal        \u00038"));
        assertThat(textClient.getTeletextLine(704, 0, 3), is("Op dit station zijn momenteel geen"));
        assertThat(textClient.getTeletextLine(704, 0, 4), is("vertrekkende treinen"));
    }

    @Test
    public void ResetMockServerTest() throws Exception {
        publicTransportModule.doTeletextUpdate();

        verify(publicTransportModule, times(1)).doTeletextUpdate();
        verify(phecapConnector, times(1)).uploadFilesToTeletextServer(any());
        Thread.sleep(1000);
        assertThat(textClient.getTeletextLine(701, 0, 0), is("\u0002MAASSLUIS"));
        textClient.resetServer();
        Thread.sleep(1000);
        assertThat(textClient.getTeletextLine(701, 0, 0), is("Error, mock server doens't contain page with pagenumber: 701"));
    }

    private String getTestData(String station) throws IOException, URISyntaxException {
        URL u = getClass().getResource("/train-departures-"+station+".xml");
        if(u == null) {
            u = getClass().getResource("/train-departures-not-exists.xml");
        }

        return String.join("", Files.readAllLines(Paths.get(u.toURI()), StandardCharsets.UTF_8));
    }

    private TrainStation initializeMockData(String trainStation, String name, String pageNumber) throws IOException, URISyntaxException{
        when(publicTransportModule.doAPICallToWebservice(trainStation)).thenReturn(getTestData(trainStation));
        return new TrainStation(trainStation, name, Short.parseShort(pageNumber), true);
    }
}