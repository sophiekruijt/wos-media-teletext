package nl.wos.teletext.dao;

import nl.wos.teletext.entity.BaseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Service
public abstract class BaseDaoOld<T extends BaseEntity<PK>, PK> {
    private static final Logger log = Logger.getLogger(String.valueOf(BaseDaoOld.class));

    public static final String PERSISTENCE_UNIT = "wosmedia";

    @PersistenceContext(unitName = PERSISTENCE_UNIT)
    private EntityManager entityManager;

    protected Class<T> entityClass;

    protected BaseDaoOld() {
        Class<?> c = getClass();
        while(!(c.getGenericSuperclass() instanceof ParameterizedType)) {
            c = c.getSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) c.getGenericSuperclass();
        entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void addCollection(Collection<T> collection) {
        for(T entity : collection) {
            add(entity);
        }
    }

    public T add(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public List<T> findAllOrderedById() {
        return findAllOrderedByProperty("id");
    }

    public List<T> findAll() {
        String entityName = entityClass.getSimpleName();
        return entityManager.createQuery(String.format("from %1$s entity", entityName)).getResultList();
    }

    public List<T> findAllOrderedByProperty(String property) {
        String entityName = entityClass.getSimpleName();
        return entityManager.createQuery(String.format("from %1$s entity order by entity.%2$s", entityName, property)).getResultList();
    }
}
