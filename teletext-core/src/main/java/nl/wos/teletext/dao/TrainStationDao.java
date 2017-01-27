package nl.wos.teletext.dao;

import nl.wos.teletext.models.TrainStation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrainStationDao {
    public List<TrainStation> getAllTrainStations();
}
