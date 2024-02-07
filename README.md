# beanDoc 工具类描述


- fieldClass.getQualifiedName()   ---- java.lang.String
- fieldClass.getName()            ---- String
- psiType.getPresentableText()    ---- List<String> 、  int[]
- psiType.type.getCanonicalText() ---- java.util.List<java.lang.String>  、int[]


##待优化事项
 
-  1.支持 Map 等类型
-  2.支持int Object 多参数,多注解 请求方式 
  
##打包方式

-- 1.找打develop-plugin -> Tasks -> build 目录下
-- 2.先进行clean、 然后再 进行build
-- 3.然后到 build目录下 --> libs目录下jar 或者 distributions目录下 **.zip

##日志路径
-- C:\Users\zhoubin\AppData\Local\JetBrains\IntelliJIdea2020.2\log

