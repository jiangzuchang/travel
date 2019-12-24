package cn.itcast.travel.web.servlet.routeListServlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.service.impl.Service;
import cn.itcast.travel.service.impl.ServiceImpl;
import cn.itcast.travel.web.servlet.basicServlet.BasicServlet;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/route/*")
public class RouteListServlet extends BasicServlet {
    /**
     * 获取路线列表
     * @param request
     * @param response
     * @throws IOException
     */
    public void getRouteList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");

        //获取cid、页码和每页条目数
        String currentPage1 = request.getParameter("currentPage");
        int currentPage = 1;//默认当前为第一页
        if (currentPage1 != null || currentPage1.length() > 0) {
            currentPage = Integer.parseInt(currentPage1);
        }
        String pageSize1 = request.getParameter("pageSize");
        int pageSize = 5;//默认每页显示5条记录
        if (pageSize1 != null || pageSize1.length() > 0) {
            pageSize = Integer.parseInt(pageSize1);
        }
        //获取cid
        String cid = request.getParameter("cid");
        //获取keyWord
        String keyWord = request.getParameter("keyWord");
        System.out.println("currentPage:" + currentPage);
        System.out.println("pageSize:" + pageSize);
        System.out.println("cid:" + cid);
        System.out.println("keyWord:" + keyWord);
        //查询数据库信息,获取当前页list集合
        Service service = new ServiceImpl();
        PageBean<Route> pageBean = service.findRoute(cid, currentPage, pageSize, keyWord);
        //设置json传输
        response.setContentType("application/json;charset=UTF-8");
        //PageBean数据转为json
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(pageBean);
        response.getWriter().write(s);
    }

    /**
     * 获取路线详情
     * @param request
     * @param response
     * @throws IOException
     */
    public void getDetailRoute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");

        //获取rid
        String rid = request.getParameter("rid");
        System.out.println("rid:" + rid);
        //获取路线详情
        Service service = new ServiceImpl();
        Route DetailRoute = service.findDetailPageMsg(rid);
        //回传数据
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(response.getWriter(), DetailRoute);


    }
}
