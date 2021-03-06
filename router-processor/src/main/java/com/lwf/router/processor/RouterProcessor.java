package com.lwf.router.processor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lwf.router.annotations.Router;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

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

        //获取所有标记了 @Router 注解的类的信息
        Set<Element> allRouterElements = (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Router.class);

        System.out.println(TAG + " >>> all Destination elements count = "
                + allRouterElements.size());

        //当未收集到 @Router 注解的时候，跳过后续流程
        if(allRouterElements.size() < 1){
            return false;
        }

        System.out.println(TAG + " >>> process start...");

        String rootDir = processingEnv.getOptions().get("root_project_dir");

        System.out.println(TAG + "rootDir= " + rootDir);

        String className = "RouterMapping_" + System.currentTimeMillis();

        StringBuilder builder = new StringBuilder();

        builder.append("package com.lwf.lrouter.mapping;\n\n");
        builder.append("import java.util.HashMap;\n");
        builder.append("import java.util.Map;\n\n");
        builder.append("public class ").append(className).append(" {\n\n");
        builder.append("    public static Map<String, String> get() {\n\n");
        builder.append("        Map<String, String> mapping = new HashMap<>();\n");

        final JsonArray pathJsonArray = new JsonArray();

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

            builder.append("        ")
                    .append("mapping.put(")
                    .append("\"").append(path).append("\"")
                    .append(", ")
                    .append("\"").append(realPath).append("\"")
                    .append(");\n");

            JsonObject object = new JsonObject();
            object.addProperty("path", path);
            object.addProperty("description", description);
            object.addProperty("realPath", realPath);

            pathJsonArray.add(object);
        }

        builder.append("        return mapping;\n");
        builder.append("    }\n");
        builder.append("}\n");

        String mappingFullClassName = "com.lwf.lrouter.mapping." + className;

        System.out.println(TAG + " >>> mappingFullClassName = " + mappingFullClassName);
        System.out.println(TAG + " >>> class content = \n" + builder);

        //写入生成类到本地文件中
        try {
            JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(mappingFullClassName);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (Exception exception){
            throw new RuntimeException(exception);
        }

        //写入JSON到本地文件中

        //检测父目录是否存在
        File rootDirFile = new File(rootDir);
        if(!rootDirFile.exists()){
            throw new RuntimeException("root_project_dir is not exist!");
        }

        //创建 router_mapping 子目录
        File routerFileDir = new File(rootDirFile, "router_mapping");
        if(!routerFileDir.exists()){
            routerFileDir.mkdir();
        }

        File mappingFile = new File(routerFileDir, "mapping_" + System.currentTimeMillis() + ".json");

        //写入json内容
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mappingFile));
            String jsonStr = pathJsonArray.toString();
            bufferedWriter.write(jsonStr);
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (Throwable throwable){
            throw new RuntimeException("Error while writing json", throwable);
        }

        System.out.println(TAG + " >>> process finish.");

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
