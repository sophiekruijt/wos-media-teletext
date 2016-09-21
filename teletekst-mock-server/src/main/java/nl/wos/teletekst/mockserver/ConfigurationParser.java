package nl.wos.teletekst.mockserver;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationParser {

    private static ConfigurationParser instance = null;

    public static ConfigurationParser getInstance() {
        if(instance == null) {
            instance = new ConfigurationParser();
        }
        return instance;
    }

    public static void main(String[] args) {
        ConfigurationParser.getInstance().parseConfiguration("[719.0000]hoihoi[719.0000]hoihoi");
    }

    public List<TeletextCommand> parseConfiguration(String configuration) {

        String pattern = "\\[.*\\].*";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(configuration);

        while (matcher.find()) {
            System.out.println("We got a result");
            System.out.println(matcher.group().trim());
        }


        return null;
    }
}
