package nl.wos.teletext.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
    Etity to be able to broadcast teletext pages like in the old situation in .NET application.
    Most database fields are not used anymore and need to be removed.
 */

@Entity
@Table(name = "items")
public class Items extends BaseEntity<String>{

    @Id
    @Column(name = "item_id")
    private String item_id;
    @Column(name = "publication_text")
    private String publication_text;
    @Column(name = "publication_title")
    private String publication_title;

    public Items() {

    }

    @Override
    protected String getPrimaryKey() {
        return item_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getPublication_text() {
        return publication_text;
    }

    public void setPublication_text(String publication_text) {
        this.publication_text = publication_text;
    }

    public String getPublication_title() {
        return publication_title;
    }

    public void setPublication_title(String publication_title) {
        this.publication_title = publication_title;
    }
}
