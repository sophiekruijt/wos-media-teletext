package nl.wos.teletekst.dao;

import nl.wos.teletekst.entity.Items;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class ItemsDao extends BaseDao<Items, String> {
    public Items findById(String id) {
        EntityManager em = super.getEntityManager();
        return (Items) em.createQuery(
                "SELECT i FROM Items i WHERE i.item_id = :id")
                .setParameter("id", id)
                .getSingleResult();
    }
}
