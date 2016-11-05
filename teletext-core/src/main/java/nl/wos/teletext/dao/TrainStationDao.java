package nl.wos.teletext.dao;

import nl.wos.teletext.entity.TrainStation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStationDao extends BaseDao {
    public List<TrainStation> getAllTrainStations() {
        SqlSession session = getSession();
        List<TrainStation> result = session.selectList("TrainStation.getAll");
        session.commit();
        session.close();
        return result;
    }
}
