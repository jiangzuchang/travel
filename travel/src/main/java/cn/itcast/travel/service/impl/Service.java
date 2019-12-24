package cn.itcast.travel.service.impl;


import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import java.io.IOException;


public interface Service {
    boolean registUser(User user);

    boolean codeActive(String code);

    User login(String username, String password);

    String findAllCategory() throws IOException;

    PageBean<Route> findRoute(String cid, int currentPage , int pageSize,String keyWord);

    Route findDetailPageMsg(String rid);

    boolean isFavorite(int uid, String cid);

    boolean beFavorite(int uid, String cid);

    PageBean<Route> findAllFavorite(int uid,int curPage,int pageSize);

    int getTotalFavorite(String rid);
}
