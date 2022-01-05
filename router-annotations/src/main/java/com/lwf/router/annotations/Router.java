package com.lwf.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ElementType.TYPE 当前注解可以修饰的元素，用于标记类（Activity）
 * RetentionPolicy.CLASS 当前注解被保留的时间为编译期
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Router {

    /**
     * 当前页面的URL，不能为空
     * @return 页面URL
     */
    String path();
    //String url() default "";//可空

    /**
     * 当前页面的中文描述
     * @return 页面描述（首页）
     */
    String description() default "";

}
