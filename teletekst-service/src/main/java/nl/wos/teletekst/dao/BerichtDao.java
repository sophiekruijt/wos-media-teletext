package nl.wos.teletekst.dao;

import nl.wos.teletekst.entity.Bericht;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class BerichtDao extends BaseDao<Bericht, String> {

    public List<Bericht> getAll() {
        List<Bericht> results = findAll();
        return results;
    }
}
