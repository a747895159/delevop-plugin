package org.zb.plugin.putil;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author : ZhouBin
 */
public class ClassUtils {


    /**
     * 获取某个包下 指定类的所有普通子类
     */
    public static List<Class<?>> findSubclassesInPackage(String packageName, Class<?> parentClass) {
        List<Class<?>> subclasses = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            ArrayList<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            for (File directory : dirs) {
                findSubclassesInDirectory(directory, packageName, parentClass, classLoader, subclasses);
            }
        } catch (IOException e) {
            // Handle the exception
        }
        return subclasses;
    }

    private static void findSubclassesInDirectory(File directory, String packageName, Class<?> parentClass, ClassLoader classLoader, List<Class<?>> subclasses) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        findSubclassesInDirectory(file, packageName + "." + file.getName(), parentClass, classLoader, subclasses);
                    } else if (file.getName().endsWith(".class")) {
                        try {
                            String classNameInFile = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> loadedClass = classLoader.loadClass(classNameInFile);
                            if (loadedClass != null && loadedClass != Object.class && loadedClass != Class.class) {
                                if (parentClass.isAssignableFrom(loadedClass) && !loadedClass.equals(parentClass)
                                        && !Modifier.isAbstract(loadedClass.getModifiers()) && !loadedClass.isInterface()) {
                                    subclasses.add(loadedClass);
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            // Handle the exception
                        }
                    }
                }
            }
        }
    }

}
