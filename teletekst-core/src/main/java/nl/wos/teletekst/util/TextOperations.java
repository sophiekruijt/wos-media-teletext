package nl.wos.teletekst.util;

import java.util.List;

public class TextOperations {
    private static int textPageSize = 39;
    private static int maxTitleSize = 35;

    public static String RemoveSpecialCharactersAndHTML(String text)
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

    // Parse een string naar een lijst (subpagina's), met lijsten met tekst per regel.
    /*public static List<List<String>> parseTekstToTeletextPageSizeArray(String tekst, int height)
    {
        List<List<string>> result = new List<List<string>>();
        result.Add(new List<string>());
        String[] words = tekst.Split(' ');

        StringBuilder builder = new StringBuilder();
        int subPage = 0;
        int line = 0;
        int counter = 0;

        for (int i = 0; i < words.Length; i++)
        {
            String word = words[i];

            if (counter + word.Length == textPageSize || counter + word.Length == textPageSize - 1)
            {
                builder.Append(word);
                counter += word.Length;
            }
            else
            if (counter + word.Length + 1 < textPageSize)
            {
                builder.Append(word + " ");
                counter += (word.Length + 1);
            }
            else
            {
                result[subPage].Add(builder.ToString());
                builder.Clear();

                counter = 0;
                line++;
                if (line > height)
                {
                    subPage++;
                    result.Add(new List<string>());
                    line = 0;
                }

                builder.Append(word + " ");
                counter += (word.Length + 1);
            }
        }
        if (!String.IsNullOrEmpty(builder.ToString()))
        {
            result[subPage].Add(builder.ToString());
            builder.Clear();
        }
        return result;
    }

    /// <summary>
    /// Berekens the aantal benodigde index paginas voor een opgeven aantal berichten en het maximaal aantal posities per index.
    /// </summary>
    public static int BerekenAantalBenodigdeIndexPaginas(int aantalBerichten, int aantalPosities)
    {
        int result = aantalBerichten / aantalPosities;
        if (aantalBerichten % aantalPosities != 0)
        {
            result++;
        }
        return result;
    }

    public static string GetStringInBetween(string stringBegin, string stringEnd, string stringSource)
    {
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
