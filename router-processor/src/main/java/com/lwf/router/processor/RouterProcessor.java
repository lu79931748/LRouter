package com.lwf.router.processor;

import com.lwf.router.annotations.Router;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by luwenfei on 2022/1/5
 * 路由注解处理器
 */
public class RouterProcessor extends AbstractProcessor {

    /**
     * 编译器找到我们关心的注解后，会回调的方法
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }

    /**
     * 告诉编译器，当前处理器支持的注解类型
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(
                Router.class.getCanonicalName()
        );
    }


}
