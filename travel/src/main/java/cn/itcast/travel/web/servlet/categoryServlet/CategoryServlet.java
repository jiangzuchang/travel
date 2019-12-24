package cn.itcast.travel.web.servlet.categoryServlet;

import cn.itcast.travel.service.impl.Service;
import cn.itcast.travel.service.impl.ServiceImpl;
import cn.itcast.travel.web.servlet.basicServlet.BasicServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/category/*")
public class CategoryServlet extends BasicServlet {
    /**
     * 处理获取类目索引的请求
     * @param request
     * @param response
     * @throws IOException
     */
    public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("收到条目申请");
        request.setCharacterEncoding("utf-8");
        Service service = new ServiceImpl();
        String allCategory = service.findAllCategory();
        System.out.println("条目信息（json）：" + allCategory);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(allCategory);
    }
}
