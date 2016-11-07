package nl.wos.teletext.dao;

import nl.wos.teletext.models.Bericht;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BerichtDao extends BaseDao {

    public List<Bericht> getAllBerichten() {
        SqlSession session = getSession();
        List<Bericht> result = session.selectList("Bericht.getAll");
        session.commit();
        session.close();
        return result;
    }
}
