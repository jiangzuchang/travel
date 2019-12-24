package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.impl.Dao;
import cn.itcast.travel.dao.impl.DaoImpl;
import cn.itcast.travel.domain.*;
import cn.itcast.travel.util.JedisUtils;
import cn.itcast.travel.util.MailUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ServiceImpl implements Service {
    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public boolean registUser(User user) {
        Dao dao = new DaoImpl();
        if (dao.findUser(user.getUsername())) {
            return false;
        } else {
            //生成激活码
            String s = "0123456789abcdefghijklmnopqrstuvwxyz";
            StringBuffer sb = new StringBuffer();
            Random random = new Random();
            for (int i = 0; i < 16; i++) {
                int index = random.nextInt(36);
                sb.append(s.charAt(index));
            }
            String code = sb.toString();
            user.setCode(code);
            //发送激活邮件
            String text = "您好。您已成功注册黑马旅游网账户，请<a href='http://localhost/travel/user/codeActive?code=" + code + "'>点此激活</a>";
            MailUtils.sendMail(user.getEmail(), text, "用户激活-【黑马旅游网】");
            return dao.save(user);
        }
    }

    /**
     * 用户激活
     * @param code
     * @return
     */
    @Override
    public boolean codeActive(String code) {
        Dao dao = new DaoImpl();
        return dao.active(code);
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) {
        Dao dao = new DaoImpl();
        User login = dao.login(username, password);
        return login;
    }

    /**
     * 获取所有索引条目
     * @return
     * @throws IOException
     */
    @Override
    public String findAllCategory() throws IOException {
        try {
            //创建Jedis对象
            Jedis jedis = JedisUtils.getJedis();
            //查询缓存
            String category = jedis.get("category");
            if (category == null || category.length() == 0) {
                //没有缓存就从数据库中查询出来
                //查询数据库
                Dao dao = new DaoImpl();
                List<Category> list = dao.findAllCategory();
                //将结果序列化为json
                ObjectMapper objectMapper = new ObjectMapper();
                String s = objectMapper.writeValueAsString(list);
                System.out.println(s);
                //存入缓存
                jedis.set("category", s);
                System.out.println("查询数据库中的Category...");
                jedis.close();
                //将查询结果返回
                return s;
            } else {
                //有缓存就直接返回缓存的数据
                System.out.println("查询缓存中的Category...");
                jedis.close();
                return category;
            }
        } catch (JedisConnectionException e) {
            System.out.println("请检查redis是否已启动，位置：D:\\redis\\redis_windows\\redis\\redis-server.exe");
            //查询数据库
            Dao dao = new DaoImpl();
            List<Category> list = dao.findAllCategory();
            //将结果序列化为json

            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(list);

            return result;
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
    public PageBean<Route> findRoute(String cid, int currentPage, int pageSize, String keyWord) {
        PageBean<Route> pageBean = new PageBean<Route>();
        Dao dao = new DaoImpl();
        //获取到查询结果list集
        List<Route> list = dao.findRoute(cid, currentPage, pageSize, keyWord);
        //获取到总的结果数
        int totalCount = dao.findCount(cid, keyWord);
        //计算结果数
        //页数
        int a = totalCount / pageSize;
        int totalPage = (totalCount % pageSize) > 0 ? (a + 1) : a;
        //封装PageBean
        pageBean.setTotalCount(totalCount);
        pageBean.setTotalPage(totalPage);
        pageBean.setCurrentPage(currentPage);
        pageBean.setList(list);
        return pageBean;
    }

    /**
     * 旅游路线详情
     * @param rid
     * @return
     */
    @Override
    public Route findDetailPageMsg(String rid) {
        Dao dao = new DaoImpl();

        Route route = dao.findDetailRoute(rid);
        Seller seller = dao.findSeller(rid);
        List<RouteImg> list = dao.findDetailImg(rid);

        System.out.println("ImgList:" + list);
        System.out.println("ImgList.length:" + list.size());

        route.setSeller(seller);
        route.setRouteImgList(list);

        return route;
    }

    /**
     * 判断是否已收藏
     * @param uid
     * @param rid
     * @return
     */
    @Override
    public boolean isFavorite(int uid, String rid) {
        Dao dao = new DaoImpl();
        Favorite favorite = dao.findFavorite(uid, rid);
        if (favorite != null) {
            return true;
        }
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
        Dao dao = new DaoImpl();
        Favorite favorite = dao.findFavorite(uid, rid);
        boolean flag;
        if (favorite != null) {
            //已收藏，取消收藏
            flag = dao.delFavorite(uid, rid);
        } else {
            //未收藏，收藏
            flag = dao.beFavorite(uid, rid);
        }
        return flag;
    }

    /**
     * 某一路线的收藏总数
     * @param uid
     * @param curPage
     * @param pageSize
     * @return
     */
    @Override
    public PageBean<Route> findAllFavorite(int uid,int curPage,int pageSize) {
        Dao dao = new DaoImpl();
        PageBean<Route> pageBean = new PageBean<Route>();
        List<Route> list = dao.findAllFavorite(uid, curPage, pageSize);
        int totalCount = dao.findFavoriteCount(uid);

        pageBean.setList(list);
        pageBean.setTotalCount(totalCount);
        return pageBean;
    }

    /**
     * 某一用户的收藏总数
     * @param rid
     * @return
     */
    @Override
    public int getTotalFavorite(String rid) {
        Dao dao = new DaoImpl();
        int count = dao.getTotalFavorite(rid);
        return count;
    }

}
