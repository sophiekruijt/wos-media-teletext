package nl.wos.teletekst.dao;

import nl.wos.teletekst.entity.TeletextPagina;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class TeletextPaginaDao extends BaseDao<TeletextPagina, Integer> {
    public TeletextPagina findPagina(int pagina) {
        EntityManager em = super.getEntityManager();
        return (TeletextPagina) em.createQuery(
                "SELECT t FROM TeletextPagina t WHERE t.pagina = :pagina")
                .setParameter("pagina", pagina)
                .getSingleResult();
    }

}
