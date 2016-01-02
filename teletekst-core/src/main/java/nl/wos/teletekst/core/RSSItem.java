package nl.wos.teletekst.core;

public class RSSItem {
    private String title;
    private String text;
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setValue(String property, String waarde) {
        switch(property) {
            case "title":
                this.title = waarde;
                break;
            case "description":
                this.text = waarde;
                break;
            case "category":
                this.category = waarde;
                break;
        }
    }
}