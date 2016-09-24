package nl.wos.teletekst.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "teletext_page")
public class TeletextPage extends BaseEntity<Integer>{

    @Id
    @Column
    private int pageNumber;
    @Column
    private String title;
    @Column
    private String text;

    public TeletextPage() {

    }

    public TeletextPage(int pageNumber, String title, String text) {
        this.pageNumber = pageNumber;
        this.title = title;
        this.text = text;
    }

    @Override
    protected Integer getPrimaryKey() {
        return pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pagina) {
        this.pageNumber = pagina;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titel) {
        this.title = titel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
