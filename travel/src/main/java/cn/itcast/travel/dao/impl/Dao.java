package cn.itcast.travel.dao.impl;

import cn.itcast.travel.domain.*;
import java.util.List;

public interface Dao {
    boolean findUser(String username);

    boolean save(User user);

    boolean active(String code);

    User login(String username, String password);

    List<Category> findAllCategory();

    List<Route> findRoute(String cid, int currentPage, int pageSize, String keyWord);

    int findCount(String cid, String keyWord);

    Route findDetailRoute(String rid);

    Seller findSeller(String rid);

    List<RouteImg> findDetailImg(String rid);

    Favorite findFavorite(int uid, String rid);

    boolean delFavorite(int uid, String rid);

    boolean beFavorite(int uid, String rid);

    List<Route> findAllFavorite(int uid,int curPage,int pageSize);

    int findFavoriteCount(int uid);

    int getTotalFavorite(String rid);
}
