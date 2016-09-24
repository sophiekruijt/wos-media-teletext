package nl.wos.teletekst.dao;

import nl.wos.teletekst.entity.TeletextPage;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class TeletextPaginaDao extends BaseDao<TeletextPage, Integer> {
    public TeletextPage findPagina(int pagina) {
        EntityManager em = super.getEntityManager();
        return (TeletextPage) em.createQuery(
                "SELECT t FROM TeletextPage t WHERE t.pageNumber = :pagina")
                .setParameter("pagina", pagina)
                .getSingleResult();
    }

}
