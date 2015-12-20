package nl.wos.teletekst.entity;

import java.io.Serializable;
import java.util.logging.Logger;

public abstract class BaseEntity<PK> implements Serializable {
    private static final long serialVersion = 1L;

    protected static final Logger log = Logger.getLogger(String.valueOf(BaseEntity.class));

    public BaseEntity() {
    }

    protected abstract PK getPrimaryKey();
}
