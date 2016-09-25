package nl.wos.teletext.mockserver;

import org.junit.Test;

import java.util.List;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class ConfigurationParserTest {
    @Test
    public void parseConfiguration() throws Exception {
        String testConfiguration = "[719.*]\n" +
                "\n" +
                "[719.0000]\n" +
                "TemplateFilename=template-departures.tpg\n" +
                "TextFilename=text-719-0.txt\n" +
                "Descr=Public transport departures\n" +
                "prompts=Nieuws Sport TV Weer\n" +
                "links=101 600 200 700\n" +
                "\n" +
                "[720.*]\n" +
                "\n" +
                "[720.0001]\n" +
                "TemplateFilename=template-sport.tpg\n" +
                "TextFilename=text-720-0.txt\n" +
                "Descr= Sport scores\n" +
                "prompts=Nieuws Sport TV Weer\n" +
                "links=101 602 200 700\n";

        ConfigurationParser configurationParser = new ConfigurationParser();
        List<TeletextCommand> teletextCommands = configurationParser.parseConfiguration(testConfiguration);

        assertThat(teletextCommands, is(not(empty())));
        assertThat(teletextCommands.size(), is(2));
        assertThat(teletextCommands.get(0).getCommand(), is("addPage"));
        assertThat(teletextCommands.get(0).getPageNumber(), is(719));
        assertThat(teletextCommands.get(0).getSubPageNumber(), is(0));
        assertThat(teletextCommands.get(0).getDescription(), is("Public transport departures"));
        assertThat(teletextCommands.get(0).getTemplateFileName(), is("template-departures.tpg"));
        assertThat(teletextCommands.get(0).getTextFileName(), is("text-719-0.txt"));
        assertThat(teletextCommands.get(1).getPageNumber(), is(720));
        assertThat(teletextCommands.get(1).getSubPageNumber(), is(1));
        assertThat(teletextCommands.get(1).getCommand(), is("addPage"));
        assertThat(teletextCommands.get(1).getDescription(), is("Sport scores"));
        assertThat(teletextCommands.get(1).getTemplateFileName(), is("template-sport.tpg"));
        assertThat(teletextCommands.get(1).getTextFileName(), is("text-720-0.txt"));
    }
}