package com.nanhang.MySpring;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/16
 * @ClassName BeanPostProcessor
 */
public interface BeanPostProcessor {
    void postProcessBeforInitialization(String BeanId,Object bean);
    void postProcessAfterInitialization(String BeanId,Object bean);
}
