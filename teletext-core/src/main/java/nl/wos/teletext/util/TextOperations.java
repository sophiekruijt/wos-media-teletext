package nl.wos.teletext.util;

import java.util.ArrayList;
import java.util.List;

public class TextOperations {

    public static final int TEXT_PAGE_SIZE = 39;
    public static final int MAX_TITLE_SIZE = 35;

    // To do: This works but is not efficient and hard to manage.
    public static String removeIllegalCharacters(String text) {
        text = text.replace("&gt;", ">");
        text = text.replace("&lt;", "<");
        text = text.replace("&#38;", "&");
        text = text.replace("&quot;", "\"");
        text = text.replace("&nbsp;", " ");
        text = text.replace("&amp;", "&");

        text = text.replace("]]>", "");
        text = text.replace("‘", "'");
        text = text.replace("’", "'");
        text = text.replace("`", "'");
        text = text.replace("´", "'");
        text = text.replace("“", "\"");
        text = text.replace("”", "\"");
        text = text.replace("³", "3");
        text = text.replace("²", "2");

        text = text.replace("ë", "e");
        text = text.replace("é", "e");
        text = text.replace("è", "e");
        text = text.replace("È", "E");
        text = text.replace("Ë", "E");
        text = text.replace("É", "E");

        text = text.replace("ä", "a");
        text = text.replace("á", "a");
        text = text.replace("à", "a");
        text = text.replace("ã", "a");
        text = text.replace("â", "a");

        text = text.replace("Á", "A");
        text = text.replace("À", "A");
        text = text.replace("Ä", "A");

        text = text.replace("í", "i");
        text = text.replace("ì", "i");
        text = text.replace("ï", "i");
        text = text.replace("Í", "I");
        text = text.replace("Ì", "I");
        text = text.replace("Ï", "I");
        text = text.replace("Î", "I");

        text = text.replace("ù", "u");
        text = text.replace("ú", "u");
        text = text.replace("ü", "u");
        text = text.replace("Ù", "u");
        text = text.replace("Ú", "U");
        text = text.replace("Ü", "U");

        text = text.replace("ö", "o");
        text = text.replace("ò", "o");
        text = text.replace("ó", "o");
        text = text.replace("Ò", "O");
        text = text.replace("Ó", "O");
        text = text.replace("Ö", "O");
        text = text.replace("Õ", "O");
        text = text.replace("Ø", "O");
        text = text.replace("Ô", "O");

        text = text.replace("†", "-");
        text = text.replace("‡", "-");
        text = text.replace("•", ".");
        text = text.replace("…", ".");
        text = text.replace("‰", "%");
        text = text.replace("™", " ");
        text = text.replace("·", ".");
        text = text.replace("˜", "");

        text = text.replace("Œ", "");
        text = text.replace("œ", "");
        text = text.replace("Š", "");
        text = text.replace("š", "");
        text = text.replace("Ÿ", "");
        text = text.replace("ƒ", "");
        text = text.replace("Ç", "");
        text = text.replace("¬", "");
        text = text.replace("¦", "");
        text = text.replace("¥", "");
        text = text.replace("§", "");
        text = text.replace("¢", "");
        text = text.replace("Þ", "");
        text = text.replace("ß", "");
        text = text.replace("¶", "");


        text = text.replace("©", "C");
        text = text.replace("®", "R");

        text = text.replace("€", "");
        text = text.replace("\r", " ");
        text = text.replace("\n", "");
        text = text.replace("–", "-");
        text = text.replace("—", "-");
        text = text.replace("<link", "");
        text = text.replace("=\"\">", "");
        text = text.replace("</span>", "");
        text = text.replace("</p>", "");
        text = text.replace("<div>", "");
        text = text.replace("</div>", "");
        text = text.replace("<DIV>", "");
        text = text.replace("</DIV>", "");
        text = text.replace("<i>", "");
        text = text.replace("</i>", "");
        text = text.replace("<I>", "");
        text = text.replace("</I>", "");
        text = text.replace("<u>", "");
        text = text.replace("</U>", "");
        text = text.replace("<U>", "");
        text = text.replace("</u>", "");
        text = text.replace("<b>", "");
        text = text.replace("</b>", "");
        text = text.replace("<B>", "");
        text = text.replace("</B>", "");
        text = text.replace("<br />", "");
        text = text.replace("<BR />", "");
        text = text.replace("<BR>", "");
        text = text.replace("<br>", "");

        text = text.replace("\u00a0", " ");
        text = text.replaceAll(" ", " ");

        text = text.replace("2016/2017 ", "");
        text = text.replace("2017/2018 ", "");
        text = text.replace("2018/2019 ", "");
        text = text.replace("2020/2021 ", "");

        return text;
    }

    public static List<List<String>> parseTextToTeletextPageSizeArray(String text, int height)
    {
        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        String[] words = text.split(" ");

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

    public static String createIndexPageTitle(String title) {
        String result;
        if (title.length() >= MAX_TITLE_SIZE) {
            result = title.substring(0, MAX_TITLE_SIZE);
        }
        else {
            result = title;
            for (int i = 0; i < MAX_TITLE_SIZE - title.length(); i++) {
                result += ".";
            }
        }
        return result;
    }
}