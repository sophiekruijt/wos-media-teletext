package nl.wos.teletext.dao;

import nl.wos.teletext.models.SportPoule;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SportPouleDao extends BaseDao {
    public List<SportPoule> getAllSportPoules() {
        SqlSession session = getSession();
        List<SportPoule> result = session.selectList("SportPoule.getAll");
        session.commit();
        session.close();
        return result;
    }
}
