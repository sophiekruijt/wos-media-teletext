package nl.wos.teletext.dao;

import nl.wos.teletext.models.SportPoule;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SportPouleDao extends BaseDao {
    public Map<String, SportPoule> getAllSportPoules() {
        SqlSession session = getSession();
        List<SportPoule> poules = session.selectList("SportPoule.getAll");
        session.commit();
        session.close();

        Map<String, SportPoule> result = new HashMap<String, SportPoule>(poules.size());
        for(SportPoule poule : poules) {
            result.put(poule.getName(), poule);
        }

        return result;
    }
}
