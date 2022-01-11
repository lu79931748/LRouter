package com.lwf.router.gradle

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

class RouterMappingTransform extends Transform {

    /**
     * 当前 Transform 的名称
     * @return
     */
    @Override
    String getName() {
        return "RouterMappingTransform"
    }

    /**
     * 返回告知编译器，当前Transform需要消费的输入类型
     * 在这里是CLASS类型
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 告知编译器，当前Transform需要收集的范围（当前工程还是所有子工程）
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否支持增量，通常返回false
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 所有的class收集好以后，会被打包传入此方法
     * @param transformInvocation
     * @throws TransformException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        //1、遍历所有的Input
        //2、对Input进行二次处理
        //3、将Input拷贝到目标目录

        RouterMappingCollector collector = new RouterMappingCollector()
        //遍历所有的输入
        transformInvocation.inputs.each {transformInput ->
            //处理文件夹类型
            transformInput.directoryInputs.each {directoryInput ->
                def destDir = transformInvocation.outputProvider.
                        getContentLocation(directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY
                        )
                collector.collect(directoryInput.file)
                //3、将Input拷贝到目标目录
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }

            //第三方依赖jar包处理
            transformInput.jarInputs.each {jarInput ->
                def dest = transformInvocation.outputProvider.
                        getContentLocation(jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes,
                                Format.JAR
                        )
                collector.collectFromJarFile(jarInput.file)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        println("${getName()} all mapping class name = "
                + collector.mappingClassName)
    }
}