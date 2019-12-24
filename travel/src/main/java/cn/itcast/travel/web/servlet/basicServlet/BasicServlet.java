package cn.itcast.travel.web.servlet.basicServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BasicServlet extends HttpServlet {
    /**
     * 所有web下具体servlet的基类，按方法分发请求处理任务
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求路径
        String requestURI = req.getRequestURI();
        //截取请求方法名
        String methodName = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        System.out.println("methodName:" + methodName);
        try {
            //获取UserServlet中的方法对象（this代表调用service的servlet类对象）
            //这里要注意UserServlet中的方法修饰符都是protected，所以要用getDeclaredMethod方法
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            //执行对应的方法
            method.invoke(this, req, resp);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
