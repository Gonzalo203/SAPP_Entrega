package es.storeapp.web.interceptors;

import es.storeapp.business.entities.User;
import es.storeapp.business.services.UserService;
import es.storeapp.common.Constants;
import es.storeapp.web.cookies.UserInfo;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.derby.iapi.services.classfile.CONSTANT_Index_info;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AutoLoginInterceptor extends HandlerInterceptorAdapter {

    private final UserService userService;

    public AutoLoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(true);
        if (request.getRequestURL().toString().contains("jsessionid")) {
            session.invalidate();
            return false;
        } 
        if (session.getAttribute(Constants.USER_SESSION) != null || 
            request.getCookies() == null ) {
            return true;
        }
        for (Cookie c : request.getCookies()) {
            if (Constants.PERSISTENT_USER_COOKIE.equals(c.getName())) {
                String cookieValue = c.getValue();
                if (cookieValue == null) {
                    continue;
                }
                Base64.Decoder decoder = Base64.getDecoder();
                XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(decoder.decode(cookieValue)));
                UserInfo userInfo = (UserInfo) xmlDecoder.readObject();
                User user = userService.findByEmail(userInfo.getEmail());
                if (user != null && user.getPassword().equals(userInfo.getPassword())) {
                    session.setAttribute(Constants.USER_SESSION, user);
                }
            }
        }
        return true;
    }
}
