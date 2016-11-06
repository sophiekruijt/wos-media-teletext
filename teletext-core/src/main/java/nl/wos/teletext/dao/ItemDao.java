package nl.wos.teletext.dao;

import nl.wos.teletext.entity.Item;
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
}
