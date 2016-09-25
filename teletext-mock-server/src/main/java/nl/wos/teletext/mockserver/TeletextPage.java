package nl.wos.teletext.mockserver;

public class TeletextPage {
    private int pageNumber;
    private int subPageNumber;
    private String[] textLines = new String[30];

    private String fastTextLabel1;
    private int fastText1;
    private String fastTextLabel2;
    private int fastText2;
    private String fastTextLabel3;
    private int fastText3;
    private String fastTextLabel4;
    private int fastText4;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getSubPageNumber() {
        return subPageNumber;
    }

    public void setSubPageNumber(int subPageNumber) {
        this.subPageNumber = subPageNumber;
    }

    public String[] getTextLines() {
        return textLines;
    }

    public void setTextLines(String[] textLines) {
        this.textLines = textLines;
    }

    public String getFastTextLabel1() {
        return fastTextLabel1;
    }

    public void setFastTextLabel1(String fastTextLabel1) {
        this.fastTextLabel1 = fastTextLabel1;
    }

    public int getFastText1() {
        return fastText1;
    }

    public void setFastText1(int fastText1) {
        this.fastText1 = fastText1;
    }

    public String getFastTextLabel2() {
        return fastTextLabel2;
    }

    public void setFastTextLabel2(String fastTextLabel2) {
        this.fastTextLabel2 = fastTextLabel2;
    }

    public int getFastText2() {
        return fastText2;
    }

    public void setFastText2(int fastText2) {
        this.fastText2 = fastText2;
    }

    public String getFastTextLabel3() {
        return fastTextLabel3;
    }

    public void setFastTextLabel3(String fastTextLabel3) {
        this.fastTextLabel3 = fastTextLabel3;
    }

    public int getFastText3() {
        return fastText3;
    }

    public void setFastText3(int fastText3) {
        this.fastText3 = fastText3;
    }

    public String getFastTextLabel4() {
        return fastTextLabel4;
    }

    public void setFastTextLabel4(String fastTextLabel4) {
        this.fastTextLabel4 = fastTextLabel4;
    }

    public int getFastText4() {
        return fastText4;
    }

    public void setFastText4(int fastText4) {
        this.fastText4 = fastText4;
    }
}
