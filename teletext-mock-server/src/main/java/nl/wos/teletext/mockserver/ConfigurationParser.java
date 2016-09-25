package nl.wos.teletext.mockserver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationParser {
    private static final Logger log = Logger.getLogger(String.valueOf(ConfigurationParser.class));
    private static ConfigurationParser instance = null;

    public static ConfigurationParser getInstance() {
        if(instance == null) {
            instance = new ConfigurationParser();
        }
        return instance;
    }

    public List<TeletextCommand> parseConfiguration(String configuration) {
        List<TeletextCommand> result = new ArrayList<>();

        String pattern = "\\[[0-9]{0,3}\\..{0,4}[^\\[]+";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(configuration);

        while (matcher.find()) {
            if(!matcher.group().trim().contains("*")) {
                // TeletextServer removes page from transmission with * command. This feature isn't implemented in this mock server, so ignore those.
                // Only other command in use besides "removePage" is the "addPage".

                String teletextCommandText = matcher.group();

                TeletextCommand teletextCommand = new TeletextCommand("addPage");
                parsePageNumber(teletextCommandText, teletextCommand);
                parseActionVariables(teletextCommandText, teletextCommand);

                result.add(teletextCommand);
            }
        }
        return result;
    }

    private void parsePageNumber(String group, TeletextCommand teletextCommand) {
        Pattern regex;
        String pageNumberPattern = "(\\[([0-9]{3}))\\.([0-9]{4})]";
        regex = Pattern.compile(pageNumberPattern);
        Matcher pageNumberMatcher = regex.matcher(group);
        if(pageNumberMatcher.find()) {
            teletextCommand.setPageNumber(Integer.parseInt(pageNumberMatcher.group(2)));
            teletextCommand.setSubPageNumber(Integer.parseInt(pageNumberMatcher.group(3)));
        }
    }

    private void parseActionVariables(String group, TeletextCommand teletextCommand) {
        Pattern regex;
        String commandPattern = "([a-zA-Z]+)=(.+)";
        regex = Pattern.compile(commandPattern);
        Matcher commandMatcher = regex.matcher(group);
        while(commandMatcher.find()) {
            String key = commandMatcher.group(1).trim();
            String value = commandMatcher.group(2).trim();

            switch(key) {
                case "TemplateFilename":
                    teletextCommand.setTemplateFileName(value);
                    break;
                case "TextFilename":
                    teletextCommand.setTextFileName(value);
                    break;
                case "Descr":
                    teletextCommand.setDescription(value);
                    break;
                case "prompts":
                    teletextCommand.setPrompts(value.split(" "));
                    break;
                case "links":
                    teletextCommand.setLinks(value.split(" "));
                    break;
                default:
                    log.warning("Unknown key ("+key+")in configuration file");
            }
        }
    }
}
