package nl.wos.teletext.dao;

import nl.wos.teletext.models.Bericht;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BerichtDao {
    public List<Bericht> getAllBerichten();
}
