package nl.wos.teletext.dao;

import nl.wos.teletext.models.Item;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemDao extends BaseDao {
    public List<Item> getAllItems() {
        SqlSession session = getSession();
        List<Item> result = session.selectList("Item.getAll");
        session.commit();
        session.close();
        return result;
    }

    public Item getItem(String id) {
        SqlSession session = getSession();
        Item item = session.selectOne("Item.getById", id);
        session.commit();
        session.close();
        return item;
    }
}
