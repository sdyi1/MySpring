package com.nanhang;

import com.nanhang.MySpring.MySpringApplicationContext;
import com.nanhang.Test01.AppConfig;


/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/11
 * @ClassName ${NAME}
 */
public class Main {
    public static void main(String[] args) {
        MySpringApplicationContext applicationContext = new MySpringApplicationContext(AppConfig.class);
    }
}