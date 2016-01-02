package nl.wos.teletekst.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "teletekst_pagina")
public class TeletextPagina extends BaseEntity<Integer>{

    @Id
    @Column
    private int pagina;
    @Column
    private String titel;
    @Column
    private String text;

    public TeletextPagina() {

    }

    public TeletextPagina(int pagina, String titel, String text) {
        this.pagina = pagina;
        this.titel = titel;
        this.text = text;
    }

    @Override
    protected Integer getPrimaryKey() {
        return pagina;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
