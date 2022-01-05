package com.lwf.router.processor;

import com.google.auto.service.AutoService;
import com.lwf.router.annotations.Router;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by luwenfei on 2022/1/5
 * 路由注解处理器
 */
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private static final String TAG = "RouterProcessor";

    /**
     * 编译器找到我们关心的注解后，会回调的方法
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //避免多次调用process
        if(roundEnvironment.processingOver()){
            return false;
        }

        System.out.println(TAG + " >>> process start...");

        //获取所有标记了 @Router 注解的类的信息
        Set<Element> allRouterElements = (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Router.class);

        System.out.println(TAG + " >>> all Destination elements count = "
                + allRouterElements.size());

        //当未收集到 @Router 注解的时候，跳过后续流程
        if(allRouterElements.size() < 1){
            return false;
        }

        //遍历所有 @Router 注解信息
        for(Element element : allRouterElements){
            final TypeElement typeElement = (TypeElement) element;

            //尝试在当前类，获取 @Router 的信息
            final Router router = typeElement.getAnnotation(Router.class);

            if(router == null) {
                continue;
            }

            final String path = router.path();
            final String description = router.description();
            //拿到全类名
            final String realPath = typeElement.getQualifiedName().toString();
            System.out.println(TAG + " >>> path = " + path);
            System.out.println(TAG + " >>> description = " + description);
            System.out.println(TAG + " >>> realPath = " + realPath);

            System.out.println(TAG + " >>> process finish.");

        }

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
