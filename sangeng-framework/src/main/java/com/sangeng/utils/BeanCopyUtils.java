package com.sangeng.utils;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

//    构造方法设置为私有的方法
    private BeanCopyUtils() {
    }
//    单个实体类拷贝(将一个源对象 拷贝 至  字节码class)
//    通过反射创建目标对象，然后再拷贝
    public static <V> V copyBean(Object source,Class<V> clazz) {
        //创建目标对象     传过来什么类型，就返回什么类型，使用泛型（  <V> V泛型方法，返回值类型 ）
        V result = null;  //提升作用域
        try {

            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //返回结果
        return result;
    }

//    集合拷贝
//  （后面要用）声明泛型O,V  返回类型     public <T> void say(){} 表明是泛型方法
    public static <O,V>  List<V> copyBeanList(List<O> list, Class<V> clazz){
//        先将list集合  转成流->流当中元素的转换(转换方式copyBean方法) 返回一个泛型V -> 收集操作，泛型转成list
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

//    测试使用方法
//    public static void main(String[] args) {
//        Article article = new Article();
//        article.setId(1L);
//        article.setTitle("hello");
//
//        HotArticleVo hotArticleVo = copyBean(article, HotArticleVo.class);
//        System.out.println(hotArticleVo);
//    }

}
