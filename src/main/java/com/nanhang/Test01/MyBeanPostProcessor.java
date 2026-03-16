package com.nanhang.Test01;

import com.nanhang.MySpring.BeanPostProcessor;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/16
 * @ClassName MyBeanPostProcessor
 */

//在MyApplication的实现逻辑是这样子的
    /*
    * 首先在扫描所有类的时候，会x先判断是否有@Component注解如果有再实现了BeanPostProcessor接口
    * 然后将实现了的添加到BeanPostProcessor集合中
    * 再初始化之前之后遍历这个集合里面所有的BeanPostProcessor对象（因为我们这里的方法参数可以看出，如果给多个对象写初始化前后，需要多个MyBeanPostProcessor对象）
    * 执行对应的方法
    * 有点类似AOP
    * */
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void postProcessBeforInitialization(String BeanId, Object bean) {

    }

    @Override
    public void postProcessAfterInitialization(String BeanId, Object bean) {

    }
}
