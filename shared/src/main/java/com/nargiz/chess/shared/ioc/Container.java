package com.nargiz.chess.shared.ioc;

import com.nargiz.chess.shared.ioc.anotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Container {
    public static Context createContext(String basePackage) {
        Context context = Context.getInstance();
        List<Class<?>> componentClasses = scanComponents(basePackage, Component.class);

        // Создаём экземпляры
        for (Class<?> clazz : componentClasses) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                context.register(clazz, instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
            }
        }

        // Внедряем зависимости
        context.injectDependencies();

        // Вызываем @PostConstruct
        context.callPostConstruct();

        return context;
    }

    public static List<Class<?>> scanComponents(String basePackage, Class<? extends Annotation> annotation) {
        List<Class<?>> components = new ArrayList<>();
        String packagePath = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (resource.getProtocol().equals("file")) {
                    for (File file : directory.listFiles()) {
                        recursiveScan(components, basePackage, file, annotation);
                    }
                }
                if (resource.getProtocol().equals("jar")) {
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    scanJar(jarPath, basePackage, components, annotation);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan package " + basePackage, e);
        }

        return components;
    }

    private static void scanJar(String jarPath, String basePackage, List<Class<?>> components, Class<? extends Annotation> annotation) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    String className = name
                            .replace(".class", "")
                            .replace('/', '.');
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotation)) {
                            System.out.println("Found component " + clazz.getCanonicalName());
                            components.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private static List<Class<?>> recursiveScan(List<Class<?>> components, String packageName, File file, Class<? extends Annotation> annotation) {
        if (file.isFile()) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." +
                        file.getName().replace(".class", "");

                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotation)) {
                        System.out.println("Found component " + clazz.getCanonicalName());
                        components.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return components;
        }
        if (file.isDirectory()) {
            String subpackage = packageName + "." + file.getName();
            for (File directoryFile : file.listFiles()) {
                recursiveScan(components, subpackage, directoryFile, annotation);
            }
            return components;
        }
        return components;
    }

}
