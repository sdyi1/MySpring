package com.nanhang.MySpring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/14
 * @ClassName scope
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface scope {
    String value() default"singleton";
    //prototype多例
}
