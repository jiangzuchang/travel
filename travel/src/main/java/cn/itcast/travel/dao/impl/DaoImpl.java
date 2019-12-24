package cn.itcast.travel.dao.impl;

import cn.itcast.travel.domain.*;
import cn.itcast.travel.util.UtilsToDruid;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;

public class DaoImpl implements Dao {
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    @Override
    public boolean findUser(String username) {
        String sql = "select * from tab_user where username=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
        try {
            jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public boolean save(User user) {
        String pattern = "yyyy-MM-dd";
        Date birthday = new Date(System.currentTimeMillis());
        String sql = "insert into " +
                "tab_user(username,password,name," +
                "birthday,sex,telephone,email,status,code) " +
                "values(?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
        jdbcTemplate.update(sql,
                user.getUsername(), user.getPassword(),
                user.getName(), birthday,
                user.getSex(), user.getTelephone(),
                user.getEmail(), "N",
                user.getCode());
        return true;
    }

    /**
     * 用户激活
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        String sql = "select * from tab_user where code=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
        try {
            //查询数据库中是否有该激活码
            jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class), code);
        } catch (DataAccessException e) {
            return false;
        }
        //激活（这里不需要防sql注入）
        String sql2 = "update tab_user set status = 'Y' where code='" + code + "'";
        jdbcTemplate.update(sql2);
        return true;
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) {
        String sql = "select * from tab_user where username=? and password=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username, password);
            return user;
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 类目索引
     * @return
     */
    @Override
    public List<Category> findAllCategory() {
        try {
            String sql = "select * from tab_category order by cid desc ";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            List<Category> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Category>(Category.class));
            return list;
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 旅游路线列表
     * @param cid
     * @param currentPage
     * @param pageSize
     * @param keyWord
     * @return
     */
    @Override
    public List<Route> findRoute(String cid, int currentPage, int pageSize, String keyWord) {
        try {
            String sql = null;
            List<Route> list = null;
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            if (cid != null && cid.length() > 0) {
                if (keyWord != null) {//分条目下查询关键字
                    sql = "select * from tab_route where cid=? and rname like \"%\"?\"%\" or routeIntroduce like \"%\"?\"%\" limit ?,?";
                    list = jdbcTemplate.query(sql,
                            new BeanPropertyRowMapper<Route>(Route.class),
                            cid, keyWord, keyWord, ((currentPage - 1) * pageSize), pageSize);
                } else {//分条目下查询
                    sql = "select * from tab_route where cid=? limit ?,?";
                    list = jdbcTemplate.query(sql,
                            new BeanPropertyRowMapper<Route>(Route.class),
                            cid, ((currentPage - 1) * pageSize), pageSize);
                }
            } else {//总条目下查询
                sql = "select * from tab_route where rname like \"%\"?\"%\" or routeIntroduce like \"%\"?\"%\"  limit ?,?";
                list = jdbcTemplate.query(sql,
                        new BeanPropertyRowMapper<Route>(Route.class),
                        keyWord, keyWord, ((currentPage - 1) * pageSize), pageSize);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 旅游路线关键字搜索
     * @param cid
     * @param keyWord
     * @return
     */
    @Override
    public int findCount(String cid, String keyWord) {
        try {
            String sql = null;
            int totalCount = 0;
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            if (cid != null && cid.length() > 0) {
                if (keyWord != null) {//分条目下查询关键字
                    System.out.println("cid和关键字不为空");
                    sql = "select count(*) from tab_route where cid=? and rname like \"%\"?\"%\" or routeIntroduce like \"%\"?\"%\"";
                    totalCount = jdbcTemplate.queryForObject(sql, Integer.class, cid, keyWord, keyWord);
                } else {//分条目下查询
                    System.out.println("cid不为空，关键字为空");
                    sql = "select count(*) from tab_route where cid=?";
                    totalCount = jdbcTemplate.queryForObject(sql, Integer.class, cid);
                }
            } else {//总条目下查询
                System.out.println("cid为空");
                sql = "select count(*) from tab_route where rname like \"%\"?\"%\" or routeIntroduce like \"%\"?\"%\"";
                totalCount = jdbcTemplate.queryForObject(sql, Integer.class, keyWord, keyWord);
            }
            return totalCount;
        } catch (DataAccessException e) {
            return 0;
        }

    }

    /**
     * 旅游路线详情
     * @param rid
     * @return
     */
    @Override
    public Route findDetailRoute(String rid) {
        try {
            String sql = "select * from tab_route where rid=?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            Route route = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Route>(Route.class), rid);
            return route;
        } catch (EmptyResultDataAccessException e) {
            System.out.println("tab_route为空");
            return null;
        }
    }

    /**
     * 商家信息检索
     * @param rid
     * @return
     */
    @Override
    public Seller findSeller(String rid) {
        try {
            String sql = "SELECT tab_route.sid,sname,consphone,address FROM tab_seller " +
                    "LEFT OUTER JOIN tab_route ON tab_route.sid = tab_seller.sid WHERE rid = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            Seller seller = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Seller>(Seller.class), rid);
            System.out.println(seller);
            return seller;
        } catch (EmptyResultDataAccessException e) {
            System.out.println("seller为空");
            return null;
        }
    }

    /**
     * 旅游路线详情页图片
     * @param rid
     * @return
     */
    @Override
    public List<RouteImg> findDetailImg(String rid) {
        try {
            String sql = "select * from tab_route_img where rid=?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            List<RouteImg> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<RouteImg>(RouteImg.class), rid);
            return list;
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 判断收藏状态
     * @param uid
     * @param rid
     * @return
     */
    @Override
    public Favorite findFavorite(int uid, String rid) {
        try {
            String sql = "select * from tab_favorite where uid = ? and rid=?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            Favorite favorite = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Favorite>(Favorite.class), uid, rid);
            return favorite;
        } catch (DataAccessException e) {
            return null;
        }

    }

    /**
     * 取消收藏
     * @param uid
     * @param rid
     * @return
     */
    @Override
    public boolean delFavorite(int uid, String rid) {
        String sql = "delete from tab_favorite where uid=? and rid=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
        jdbcTemplate.update(sql, uid, rid);
        return false;
    }

    /**
     * 收藏
     * @param uid
     * @param rid
     * @return
     */
    @Override
    public boolean beFavorite(int uid, String rid) {
        String sql = "insert into tab_favorite(rid,date,uid) values(?,?,?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        jdbcTemplate.update(sql, Integer.parseInt(rid), date, uid);
        return true;
    }

    /**
     * 我的收藏
     * @param uid
     * @param curPage
     * @param pageSize
     * @return
     */
    @Override
    public List<Route> findAllFavorite(int uid, int curPage, int pageSize) {
        try {
            String sql = "SELECT * FROM tab_route" +
                    " INNER JOIN tab_favorite ON tab_favorite.rid=tab_route.rid where uid=? limit ?,?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            List<Route> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class), uid, (curPage - 1) * pageSize, pageSize);
            return list;
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 某一用户的总收藏数
     * @param uid
     * @return
     */
    @Override
    public int findFavoriteCount(int uid) {
        try {
            String sql = "SELECT count(*) FROM tab_route" +
                    " INNER JOIN tab_favorite ON tab_favorite.rid=tab_route.rid where uid=?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            int totalCount = jdbcTemplate.queryForObject(sql, Integer.class, uid);
            return totalCount;
        } catch (DataAccessException e) {
            return 0;
        }
    }

    /**
     * 某一路线的总收藏数
     * @param rid
     * @return
     */
    @Override
    public int getTotalFavorite(String rid) {
        try {
            String sql ="select count(*) from tab_favorite where rid=?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(UtilsToDruid.getDataSource());
            int count = jdbcTemplate.queryForObject(sql, Integer.class, rid);
            return count;
        }catch (DataAccessException e){
            return 0;
        }
    }

}
