package com.nanhang.MySpring2;

import com.nanhang.MySpring.Component;
import com.nanhang.MySpring.ComponentScan;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/11
 * @ClassName MySpringApplicationContext
 */
public class MySpringApplicationContext {
    private Class configClass;

    public MySpringApplicationContext(Class configClass) {
        this.configClass = configClass;
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan ComponentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String Path = ComponentScan.value();
            Path = Path.replace(".", "/");
            ClassLoader classLoader = configClass.getClassLoader();
            URL resource = classLoader.getResource(Path);
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {
                    /*为什么上面路径是/D:/JavaSpring6/MySpring/target/classes/com/nanhang/Test01
                        这里是D:\JavaSpring6\MySpring\target\classes\com\nanhang\Test01\AppConfig.class
                        注意看这里已经循环数组了，数组里面是Test01里面的所有文件的类路径
                    * */
                    String fileName = file1.getAbsolutePath();

                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));//com/nanhang/Test01.
                        className = className.replace("\\", ".");
                        Class<?> fileClazz = null;
                        try {
                            fileClazz = classLoader.loadClass(className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        if (fileClazz.isAnnotationPresent(Component.class)) {
                        }
                    }
                }
            }
        }
    }

    public Object getBean(String BeanId) {

        return null;
    }
}
