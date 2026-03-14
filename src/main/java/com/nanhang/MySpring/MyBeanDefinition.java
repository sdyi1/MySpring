package com.nanhang.MySpring;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/14
 * @ClassName MyBeanDefinition
 */
public class MyBeanDefinition {
    private Class clazz;
    private String Scope;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String scope) {
        Scope = scope;
    }
}
