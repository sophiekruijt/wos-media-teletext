package nl.wos.teletext.dao;

import nl.wos.teletext.entity.Items;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class ItemsDao extends BaseDaoOld<Items, String> {
    public Items findById(String id) {
        EntityManager em = super.getEntityManager();
        return (Items) em.createQuery(
                "SELECT i FROM Items i WHERE i.item_id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }
}
