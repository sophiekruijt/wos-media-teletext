package nl.wos.teletext.dao;

import nl.wos.teletext.entity.TeletextPage;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class TeletextPaginaDao extends BaseDaoOld<TeletextPage, Integer> {
    public TeletextPage findPagina(int pagina) {
        EntityManager em = super.getEntityManager();
        return (TeletextPage) em.createQuery(
                "SELECT t FROM TeletextPage t WHERE t.pageNumber = :pagina")
                .setParameter("pagina", pagina)
                .getSingleResult();
    }

}
