package cn.itcast.travel.web.servlet.userServlet;

import cn.itcast.travel.domain.*;
import cn.itcast.travel.service.impl.Service;
import cn.itcast.travel.service.impl.ServiceImpl;
import cn.itcast.travel.web.servlet.basicServlet.BasicServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@WebServlet("/user/*")
public class UserServlet extends BasicServlet {
    private Map<String, Boolean> map = new HashMap<String, Boolean>();

    /**
     * 验证码生成
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void checkCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        //设置响应头的ContentType参数值
        response.setContentType("text/html;charset=utf-8");
        //设置验证码图片宽高
        int width = 100;
        int height = 28;
        //创建图片缓存对象
        BufferedImage bfi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //创建该图片上的画图工具对象
        Graphics g = bfi.getGraphics();
        //使用画图工具对象设置画笔颜色、填充图片
        Color c1 = new Color(61, 61, 63);
        g.setColor(c1);
        g.fillRect(0, 0, width, height);
        //设置画笔色，画出边框
        g.setColor(c1);
        g.drawRect(0, 0, width - 1, height - 1);
        //创建随机数对象
        Random random = new Random();
        //画干扰线
        Color c2 = new Color(255, 255, 0);
        g.setColor(c2);
        g.setFont(new Font("GB2312", 3, 18));
        for (int i = 0; i < 6; i++) {
            int x1 = random.nextInt(width - 1);
            int x2 = random.nextInt(width - 1);
            int y1 = random.nextInt(height - 1);
            int y2 = random.nextInt(height - 1);
            g.drawLine(x1, y1, x2, y2);
        }
        //画验证码
        char[] chars = new char[4];
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 4; i++) {
            int a = random.nextInt(str.length());
            chars[i] = str.charAt(a);
            String c = String.valueOf(chars[i]);
            g.drawString(c, ((width / 4) * i) + 8, (height / 2) + random.nextInt(7));
        }
        //将图片通过request获取的OutputStream输出
        ImageIO.write(bfi, "png", response.getOutputStream());

        String checkCode_real = String.valueOf(chars);
        System.out.println(checkCode_real);

        HttpSession session = request.getSession();

        session.setAttribute("checkCode_real", (checkCode_real.toLowerCase()));
    }


    /**
     * 账户登录
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void userLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        ResultInfo ris = new ResultInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        boolean flag = false;

        HttpSession session = request.getSession();
        String checkCode_real = (String) session.getAttribute("checkCode_real");
        String check = request.getParameter("check").toLowerCase();

        if (check.equals(checkCode_real)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            Service service = new ServiceImpl();
            User user = service.login(username, password);
            if (user == null) {
                //登录失败
                ris.setErrorMsg("账号或密码错误");
                System.out.println("登录失败");
            } else if ("Y".equalsIgnoreCase(user.getStatus())) {
                //登录成功,这里可能要存数据，暂留
                session.setAttribute("user", user);
                flag = true;
                System.out.println("登录成功");
            } else {
                //登录失败
                ris.setErrorMsg("您的账户未激活");
                System.out.println("账户未激活");
            }
        } else {
            //验证码错误
            ris.setErrorMsg("验证码错误");
            System.out.println("验证码错误");
        }
        response.setContentType("application/json;charset=utf-8");
        ris.setFlag(flag);
        String s = objectMapper.writeValueAsString(ris);
        System.out.println(s);
        response.getWriter().write(s);
    }

    /**
     * 获取用户名
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void findUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        ResultInfo ris = new ResultInfo();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            ris.setFlag(true);
            ris.setData(user.getName());
        } else {
            ris.setFlag(false);
        }
        response.setContentType("application/json;charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(ris);
        System.out.println(s);
        response.getWriter().write(s);
    }

    /**
     * 账户激活
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void codeActive(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        if (code != null) {
            //有激活码，尝试激活
            Service service = new ServiceImpl();
            if (service.codeActive(code)) {
                //激活成功
                String msg = "激活成功，请<a href='http://localhost/travel/login.html'>登录</a>";
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write(msg);
            } else {
                //激活失败
                response.sendRedirect("http://localhost/travel/fail.html");
            }
        } else {
            //空的激活码，劝退
            response.sendRedirect("http://localhost/travel/warning.html");
        }
    }

    /**
     * 账户注册
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        System.out.println("收到注册请求");
        //创建返回对象
        ResultInfo rsi = new ResultInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        String checkCode_client = request.getParameter("check");
        String checkCode_server = (String) request.getSession().getAttribute("checkCode_real");
        boolean flag = checkCode_server.equalsIgnoreCase(checkCode_client);
        //先校验验证码
        if (flag) {
            System.out.println("验证码正确");
            //把表单数据存为对象
            User user = new User();
            Map<String, String[]> map_user = request.getParameterMap();
            try {
                BeanUtils.populate(user, map_user);
                System.out.println(user);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            Service service = new ServiceImpl();
            boolean flag_save = service.registUser(user);
            if (flag_save) {
                //注册成功
                rsi.setFlag(true);
                System.out.println("注册成功");
            } else {
                //注册失败
                System.out.println("注册失败");
                //返给客户端，告诉客户端这个用户名已经被注册了
                flag = false;
                rsi.setErrorMsg("该用户名已被注册");
                System.out.println(rsi);
                rsi.setFlag(false);
            }
        } else {
            System.out.println("验证码错误");
            //返给客户端，验证码错误
            rsi.setErrorMsg("验证码错误");
            rsi.setFlag(false);
        }
        response.setContentType("application/json;charset=utf-8");
        //序列化为json
        String s = objectMapper.writeValueAsString(rsi);
        System.out.println(s);
        //返回给客户端
        response.getWriter().write(s);
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void outAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        session.removeAttribute("user");
    }

    /**
     * 是否已收藏
     * @param request
     * @param response
     * @throws IOException
     */
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        boolean flag = false;
        //判断有没有登录
        if (user != null) {
            //已经登录了
            //查看收藏状态
            User user1 = (User) user;
            int uid = user1.getUid();
            String rid = request.getParameter("rid");
            Service service = new ServiceImpl();
            flag = service.isFavorite(uid, rid);
        }
        map.put("flag", flag);
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(map);
        response.getWriter().write(s);
    }

    /**
     * 收藏
     * @param request
     * @param response
     * @throws IOException
     */
    public void beFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();

        boolean flag = false;
        boolean lanchStatus = false;
        Object user = session.getAttribute("user");
        //判断有没有登录
        if (user != null) {
            //已经登录了
            //改变收藏状态
            lanchStatus = true;
            User user1 = (User) user;
            int uid = user1.getUid();
            String rid = request.getParameter("rid");
            Service service = new ServiceImpl();
            flag = service.beFavorite(uid, rid);
        }
        map.put("flag", flag);
        map.put("lanchStatus", lanchStatus);
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(map);
        response.getWriter().write(s);
    }

    /**
     * 我的收藏
     * @param request
     * @param response
     * @throws IOException
     */
    public void findAllFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        int curPage = Integer.parseInt(request.getParameter("curPage"));
        if (pageSize <= 0) {
            pageSize = 8;
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        ResultInfo resultInfo = new ResultInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        if (user != null) {
            //已登录
            int uid = user.getUid();
            Service service = new ServiceImpl();
            PageBean<Route> list = service.findAllFavorite(uid, curPage, pageSize);
            resultInfo.setFlag(true);
            resultInfo.setData(list);
        } else {
            //未登录
            resultInfo.setFlag(false);
        }
        String s = objectMapper.writeValueAsString(resultInfo);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    /**
     * 某路线的总收藏数
     * @param request
     * @param response
     * @throws IOException
     */
    public void totalFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        Map<String, String> map = new HashMap<String, String>();
        String rid = request.getParameter("rid");
        Service service = new ServiceImpl();
        int totalFavorite = service.getTotalFavorite(rid);
        map.put("count", String.valueOf(totalFavorite));
        response.setContentType("application/json;charset=utf-8");
        String s = new ObjectMapper().writeValueAsString(map);
        response.getWriter().write(s);
    }
}
