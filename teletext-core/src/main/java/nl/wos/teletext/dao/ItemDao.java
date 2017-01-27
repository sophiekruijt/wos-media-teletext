package nl.wos.teletext.dao;

import nl.wos.teletext.models.Item;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemDao {
    public List<Item> getAllItems();
    public Item getItem(String id);
}
