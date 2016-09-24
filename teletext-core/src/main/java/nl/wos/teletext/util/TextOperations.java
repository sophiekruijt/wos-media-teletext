package nl.wos.teletext.util;

import java.util.ArrayList;
import java.util.List;

public class TextOperations {

    public static final int TEXT_PAGE_SIZE = 39;
    public static final int MAX_TITLE_SIZE = 35;

    public static String removeSpecialCharactersAndHTML(String text) {
        String result = text;

        result = result.replace("&gt;", ">");
        result = result.replace("&lt;", "<");
        result = result.replace("&#38;", "&");
        result = result.replace("&quot;", "\"");
        result = result.replace("&nbsp;", " ");
        result = result.replace("&amp;", "&");

        result = result.replace("]]>", "");
        result = result.replace("‘", "'");
        result = result.replace("’", "'");
        result = result.replace("`", "'");
        result = result.replace("´", "'");
        result = result.replace("“", "\"");
        result = result.replace("”", "\"");
        result = result.replace("³", "3");
        result = result.replace("²", "2");

        result = result.replace("ë", "e");
        result = result.replace("é", "e");
        result = result.replace("è", "e");
        result = result.replace("È", "E");
        result = result.replace("Ë", "E");
        result = result.replace("É", "E");

        result = result.replace("ä", "a");
        result = result.replace("á", "a");
        result = result.replace("à", "a");
        result = result.replace("ã", "a");
        result = result.replace("â", "a");

        result = result.replace("Á", "A");
        result = result.replace("À", "A");
        result = result.replace("Ä", "A");

        result = result.replace("í", "i");
        result = result.replace("ì", "i");
        result = result.replace("ï", "i");
        result = result.replace("Í", "I");
        result = result.replace("Ì", "I");
        result = result.replace("Ï", "I");
        result = result.replace("Î", "I");

        result = result.replace("ù", "u");
        result = result.replace("ú", "u");
        result = result.replace("ü", "u");
        result = result.replace("Ù", "u");
        result = result.replace("Ú", "U");
        result = result.replace("Ü", "U");

        result = result.replace("ö", "o");
        result = result.replace("ò", "o");
        result = result.replace("ó", "o");
        result = result.replace("Ò", "O");
        result = result.replace("Ó", "O");
        result = result.replace("Ö", "O");
        result = result.replace("Õ", "O");
        result = result.replace("Ø", "O");
        result = result.replace("Ô", "O");

        result = result.replace("†", "-");
        result = result.replace("‡", "-");
        result = result.replace("•", ".");
        result = result.replace("…", ".");
        result = result.replace("‰", "%");
        result = result.replace("™", " ");
        result = result.replace("·", ".");
        result = result.replace("˜", "");

        result = result.replace("Œ", "");
        result = result.replace("œ", "");
        result = result.replace("Š", "");
        result = result.replace("š", "");
        result = result.replace("Ÿ", "");
        result = result.replace("ƒ", "");
        result = result.replace("Ç", "");
        result = result.replace("¬", "");
        result = result.replace("¦", "");
        result = result.replace("¥", "");
        result = result.replace("§", "");
        result = result.replace("¢", "");
        result = result.replace("Þ", "");
        result = result.replace("ß", "");
        result = result.replace("¶", "");


        result = result.replace("©", "C");
        result = result.replace("®", "R");

        result = result.replace("€", "");
        result = result.replace("\r", " ");
        result = result.replace("\n", "");
        result = result.replace("–", "-");
        result = result.replace("—", "-");
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
        result = result.replace("<br />", "");
        result = result.replace("<BR />", "");
        result = result.replace("<BR>", "");
        result = result.replace("<br>", "");

        result = result.replace("\u00a0", " ");
        result = result.replaceAll(" ", " ");

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

            if (counter + word.length() ==  TEXT_PAGE_SIZE || counter + word.length()  ==  TEXT_PAGE_SIZE - 1) {
                builder.append(word);
                counter += word.length();
            }
            else if (counter + word.length() + 1 < TEXT_PAGE_SIZE) {
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
        if (publicationTitle.length() >= MAX_TITLE_SIZE) {
            result = publicationTitle.substring(0, MAX_TITLE_SIZE);
        }
        else {
            result = publicationTitle;
            for (int i = 0; i < MAX_TITLE_SIZE - publicationTitle.length(); i++) {
                result += ".";
            }
        }
        return result;
    }
}
