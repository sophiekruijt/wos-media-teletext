package nl.wos.teletekst.util;

import java.util.ArrayList;
import java.util.List;

public class TextOperations {
    private static int textPageSize = 39;
    private static int maxTitleSize = 35;

    public static String removeSpecialCharactersAndHTML(String text)
    {
        String result = text.toString();

        result = result.replace("<br />", "");
        result = result.replace("<BR />", "");
        result = result.replace("<BR>", "");
        result = result.replace("<br>", "");
        result = result.replace("&gt;", ">");
        result = result.replace("&lt;", "<");
        result = result.replace("&#38;", "&");
        result = result.replace("]]>", "");
        result = result.replace("‘", "'");
        result = result.replace("`", "'");
        result = result.replace("’", "'");
        result = result.replace("è", "e");
        result = result.replace("È", "E");
        result = result.replace("Ë", "E");
        result = result.replace("é", "e");
        result = result.replace("É", "E");
        result = result.replace("ë", "e");
        result = result.replace("á", "a");
        result = result.replace("à", "a");
        result = result.replace("ä", "a");
        result = result.replace("Á", "A");
        result = result.replace("À", "A");
        result = result.replace("Ä", "A");
        result = result.replace("í", "i");
        result = result.replace("ì", "i");
        result = result.replace("Í", "I");
        result = result.replace("Ì", "I");
        result = result.replace("Ï", "I");
        result = result.replace("ï", "i");
        result = result.replace("ü", "u");
        result = result.replace("ù", "u");
        result = result.replace("ú", "u");
        result = result.replace("Ù", "u");
        result = result.replace("Ú", "U");
        result = result.replace("Ü", "U");
        result = result.replace("ò", "o");
        result = result.replace("ó", "o");
        result = result.replace("Ò", "O");
        result = result.replace("Ó", "O");
        result = result.replace("Ö", "O");
        result = result.replace("ö", "o");
        result = result.replace("&quot;", "\"");
        result = result.replace("&nbsp;", " ");
        result = result.replace("&amp;", "&");
        result = result.replace("€", "");
        result = result.replace("“", "\"");
        result = result.replace("”", "\"");
        result = result.replace("\r", " ");
        result = result.replace("\n", "");
        result = result.replace("–", "-");
        result = result.replace("<link", "");
        result = result.replace("=\"\">", "");
        result = result.replace("</span>", "");
        result = result.replace("</p>", "");
        result = result.replace("<div>", "");
        result = result.replace("</div>", "");
        result = result.replace("<DIV>", "");
        result = result.replace("</DIV>", "");
        result = result.replace("<i>", "");
        result = result.replace("</i>", "");
        result = result.replace("<I>", "");
        result = result.replace("</I>", "");
        result = result.replace("<u>", "");
        result = result.replace("</U>", "");
        result = result.replace("<U>", "");
        result = result.replace("</u>", "");
        result = result.replace("<b>", "");
        result = result.replace("</b>", "");
        result = result.replace("<B>", "");
        result = result.replace("</B>", "");

        return result;
    }

    // Parse een string naar een lijst (subpagina's), met lijsten met strings per regel.
    public static List<List<String>> parseTekstToTeletextPageSizeArray(String tekst, int height)
    {
        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        String[] words = tekst.split(" ");

        StringBuilder builder = new StringBuilder();
        int subPage = 0;
        int line = 0;
        int counter = 0;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (counter + word.length() == textPageSize || counter + word.length()  == textPageSize - 1) {
                builder.append(word);
                counter += word.length();
            }
            else if (counter + word.length() + 1 < textPageSize) {
                builder.append(word + " ");
                counter += (word.length() + 1);
            }
            else {
                result.get(subPage).add(builder.toString());
                builder.setLength(0);

                counter = 0;
                line++;
                if (line > height) {
                    subPage++;
                    result.add(new ArrayList<>());
                    line = 0;
                }
                builder.append(word + " ");
                counter += (word.length() + 1);
            }
        }
        if (!builder.toString().isEmpty()) {
            result.get(subPage).add(builder.toString());
            builder.setLength(0);
        }
        return result;
    }

    /*public static string GetStringInBetween(string stringBegin, string stringEnd, string stringSource) {
        string result = "";

        int start1 = stringSource.IndexOf(stringBegin);
        if (start1 == -1)
        {
            return "";
        }
        int end1 = stringSource.IndexOf(stringEnd);
        if (end1 == -1)
        {
            return "";
        }
        result = stringSource.Substring(start1 + stringBegin.Length, end1 - start1 - stringBegin.Length);

        return result;
    }*/

    public static String makeBerichtTitelVoorIndexPagina(String publicationTitle) {
        String result;

        if (publicationTitle.length() >= maxTitleSize) {
            result = publicationTitle.substring(0, maxTitleSize);
        }
        else {
            result = publicationTitle;
            for (int i = 0; i < maxTitleSize - publicationTitle.length(); i++) {
                result += ".";
            }
        }
        return result;
    }
}
