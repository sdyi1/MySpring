package com.nanhang.Test01;

import com.nanhang.MySpring.BeanNameAware;
import com.nanhang.MySpring.Component;
import com.nanhang.MySpring.InitializingBean;
import com.nanhang.MySpring.scope;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/13
 * @ClassName UserService
 */
@scope()
@Component()
public class UserService implements BeanNameAware , InitializingBean {
    private UserDao userDao;
    private String beanName;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void setBeanName(String BeanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSwt() {

    }
}
