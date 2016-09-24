package nl.wos.teletext.dao;

import nl.wos.teletext.entity.Bericht;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class BerichtDao extends BaseDao<Bericht, String> {

    public List<Bericht> getAll() {
        List<Bericht> results = findAll();
        return results;
    }
}
