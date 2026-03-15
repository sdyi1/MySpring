package com.nanhang.MySpring;

import javax.sound.midi.Soundbank;
import java.beans.BeanDescriptor;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author 徐
 * @version 1.0
 * @target
 * @date 2026/3/11
 * @ClassName MySpringApplicationContext
 */
public class MySpringApplicationContext {
    //我们创建ApplicationContext的时候，一般会传入一个配置类或者配置文件，所以我们这里需要一个成员变量
    private Class configClass;
    //为什么使用ConcurrentMap,因为线程安全，只有再第一个执行完成后才会执行第二个
    private ConcurrentMap<String,MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //创建单例集合，用于存放单例的实例化对象
    private ConcurrentMap<String,Object> singletonObjectMap = new ConcurrentHashMap<>();

    public MySpringApplicationContext(Class configClass) {
        this.configClass = configClass;
    System.out.println("构造方法执行");
        //判断传入的配置类是否有配置类注解 ComponentScan注解
        if (configClass.isAnnotationPresent(ComponentScan.class)){
//1
            //如果有这个注解的就获取这个注解里面的信息（我们要获取这个注解对象通过这个注解里面的信息来判断扫描@Component的范围）
            ComponentScan ComponentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
           //获取里面的成员变量
            String Path = ComponentScan.value();//com.nanhang.Test01

            //Spring容器是再运行的时候工作的，所以扫描也是再运行的时候扫描，我们需要给他扫描的路径是类路径
            Path = Path.replace(".", "/");//com/nanhang/Test01

            //获取配置类的类加载器，
            ClassLoader classLoader = configClass.getClassLoader();
            //这个方法会再类路径下面查找我们传入的Path,返回一个Path的全类路径,然后我们就可以使用这个全类路径扫描需要加入IOC容器的类
            URL resource = classLoader.getResource(Path); //D:\.../.../.../Test01/...
            //获取了URL接下来我们需要遍历这个文件下面的所有文件(遍历文件就想到了File)，找出上面标注了@Component,@Service ... ...这些注解的类
               /*   resource 是一个 URL 对象，它的 toString() 返回：
                    "file:/D:/JavaSpring6/MySpring/target/classes/com/nanhang/Test01/"
                    这个字符串包含协议前缀 "file:"，不能直接传给 File 构造函数*/
                    //resource.getFile()去掉协议前缀 变成/D:/JavaSpring6/MySpring/target/classes/com/nanhang/Test01/"
            File file =new File(resource.getFile()) ;



                //判断file是不是文件
                if(file.isDirectory()){
                    //获取file下面的所有文件,传入数组
                    File[] files = file.listFiles();
                    for (File file1 : files) {
                        /*file1.getName()  获取文件名字 带后缀*/
                        String fileName = file1.getAbsolutePath();//获取文件绝对路径

                        //判断后缀是不是.class,是的话说明这个是javaclass文件，就可以判断里面是否有@Component,@Service这些注解了
                        if (fileName.endsWith(".class")){
                        /*
                        * 判断这个类是不是Bean 如何判断
                        *   类上面是否有@Component等注解，这时发现需要类的class对象来调用判断方法isAnnotationPresent
                        * 如何获取文件的类对象
                        * 通过类加载器先获取类的相对路径 com.nanhang
                        * */
                            //通过类加载器和文件名获取类对象 需要我文件路径是包路径 com.nanhang..... ...
                            String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));//com/nanhang/Test01.
                            className = className.replace("\\", ".");

                            //接下来我们需要通过类加载器和包路径获取类对象
                            Class<?> fileClazz = null;
                            try {
                                fileClazz = classLoader.loadClass(className);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            //判断是否有Bean注解
                            if (fileClazz.isAnnotationPresent(Component.class)){
                                //获取Component的value，给后面存入Map当BeanID
                                String beanId = fileClazz.getAnnotation(Component.class).value();
                                //判断value是否为null 或者空字符串，或者为制表符空格,不为null就直接当Beanid,为null就反射出Bean的名字小写首字母
                                if (beanId ==null||beanId.isEmpty()||beanId.trim().isEmpty()){
                                    String simpleName = fileClazz.getSimpleName();
                                    beanId = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
                                }

                                //到这里就说明这个类是Bean类，接下来就是判断是否为单例还是多例
                                MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
                                //使用循环到的class对象，判断是否又scope注解
                                myBeanDefinition.setClazz(fileClazz);
                                //判断这个类是否有scope注解
                                if (fileClazz.isAnnotationPresent(scope.class)){
                                    //如果有就获取注解里面的值，赋值给myBeanDefinition
                                    myBeanDefinition.setScope(fileClazz.getAnnotation(scope.class).value());
                                }else {
                                    //没有这个注解就直接赋值单例
                                    myBeanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap  .put(beanId, myBeanDefinition);
                            }
                        }
                    }
                }
        }
                    //遍历beanDefinitionMap，如果有单例的就创建对象，放入单例Map中
        for (String beanName : beanDefinitionMap.keySet()) {
           //通过Map的key获取value
            MyBeanDefinition myBeanDefinition = beanDefinitionMap.get(beanName);
            //获取循环到的MyBeanDefinition，获取里面的scope值，判断是不是单例
            String scope = myBeanDefinition.getScope();
            //如果是单例就实例化放入单例集合
            if (scope.equals("singleton")){
                Object bean = createBean(beanName, myBeanDefinition);
                //放入单例集合
                singletonObjectMap.put(beanName,bean);
            }
        }
        //为什么不用这个，后面将单例放入单例集合的时候需要用到key，这个循环没有获取key
       /* for (MyBeanDefinition value : beanDefinitionMap.values()) {

        }*/
    }


    //
    public Object createBean (String beanName,MyBeanDefinition myBeanDefinition){
        Class clazz = myBeanDefinition.getClazz();
        try {
            //获取构造方法
            Constructor declaredConstructor = clazz.getDeclaredConstructor();
            //开启权限，可以访问所有方法 包括私有方法
            declaredConstructor.setAccessible(true);
            try {
                //使用构造方法实例化对象
                Object bean = declaredConstructor.newInstance();

                //获取对象里面的所有成员变量,循环判断是否包含@Autowired注解
                for (Field field : bean.getClass().getDeclaredFields()) {
                    //判断是否带有Autowired注解，有就赋值(通过变量名字)
                    if (field.isAnnotationPresent(Autowired.class)) {
                        //注入    成员变量.set（类，注入的值）
                            //假设 userService的成员变量UserDao
                                // 如果是多例，USerDap还没有实例化，在getBean的时候会实例化
                                //如果是单例，会先通过成员变量名称查询单例Map，如果没有，会调用createBean创建对象,为什么可以成功创建不是需要两个参数吗，
                        //      参数1我们传入了就是成员变量的名字，参数2只要我们在UserDao上面添加了@Component注解就在构造方法中就创建了对应的MyBeanDefinition对象放入MyBeanDefinitionMap了
                        field.setAccessible(true);
                        field.set(bean,getBean(field.getName()));
                    }
                }
                return bean;
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String BeanId){
        MyBeanDefinition myBeanDefinition = beanDefinitionMap.get(BeanId);
        //判断Map里面有没有这个BeanId的数据没有就抛出异常
        if (myBeanDefinition==null){
            throw new NullPointerException();
        }else {
            //如果有就判断是不是单例
            if (myBeanDefinition.getScope().equals("singleton")){
                //如果是单例，就从单例map里面获取
                Object singletonBean = singletonObjectMap.get(BeanId);
                //判断对象是否存在,使用createBean创建对象，补充加入map
                if (singletonBean==null){
                    singletonBean = createBean(BeanId, myBeanDefinition);
                    //加入单例Map
                    singletonObjectMap.put(BeanId,singletonBean);
                }
                //如果存在就return
                return singletonBean;

            }else{
                //如果是多例就直接创建，不用从单例池子拿取，用户new一个就创建一个
                return createBean(BeanId,myBeanDefinition);
            }
        }
    }
}
