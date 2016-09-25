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
    @Column(name = "itemId")
    private String itemId;
    @Column(name = "publicationText")
    private String publicationText;
    @Column(name = "publicationTitle")
    private String publicationTitle;

    public Items() {

    }

    @Override
    protected String getPrimaryKey() {
        return itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPublicationText() {
        return publicationText;
    }

    public void setPublicationText(String publicationText) {
        this.publicationText = publicationText;
    }

    public String getPublicationTitle() {
        return publicationTitle;
    }

    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }
}
