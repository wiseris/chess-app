package com.nargiz.chess.shared.ioc;

import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class Context {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Class<?>> interfaceToImpl = new HashMap<>();

    private static Context instance;

    private Context() {

    }

    public static Context getInstance() {
        if (instance == null) {
            synchronized (Context.class) {
                if (instance == null) {
                    instance = new Context();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        // Сначала ищем прямой маппинг
        Object instance = instances.get(type);
        if (instance != null) {
            return (T) instance;
        }

        // Затем ищем по интерфейсам
        Class<?> impl = interfaceToImpl.get(type);
        if (impl != null) {
            return (T) instances.get(impl);
        }

        return null;
    }

    public void register(Class<?> type, Object instance) {
        instances.put(type, instance);

        // Регистрируем ВСЕ интерфейсы, которые реализует класс
        registerAllInterfaces(type, type);

        // Также регистрируем интерфейсы суперкласса
        Class<?> superclass = type.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            registerAllInterfaces(superclass, type);
        }
    }

    public void addNewInstance(Class<?> type, Object instance) {
        register(type, instance);
        injectDependencies();

        invokePostConstruct(instance);
    }

    private void registerAllInterfaces(Class<?> targetClass, Class<?> implClass) {
        for (Class<?> iface : targetClass.getInterfaces()) {
            interfaceToImpl.put(iface, implClass);
            // Рекурсивно регистрируем родительские интерфейсы
            registerAllInterfaces(iface, implClass);
        }
    }

    public void injectDependencies() {
        for (Object instance : instances.values()) {
            injectInto(instance);
        }
    }

    private static List<Field> getAllFields(Class<?> instance) {
        List<Field> result = new ArrayList<>();

        while (instance != null && instance != Object.class) {
            result.addAll(Arrays.asList(instance.getDeclaredFields()));
            instance = instance.getSuperclass();
        }
        return result;
    }

    private void injectInto(Object instance) {
        for (Field field : getAllFields(instance.getClass())) {
            if (field.isAnnotationPresent(Inject.class)) {
                Object dependency = get(field.getType());
                if (Collection.class.isAssignableFrom(field.getType())) {
                    dependency = getAsCollection(field);
                }
                injectDependency(instance, field, dependency);
            }
        }
    }

    private Collection getAsCollection(Field field) {
        ParameterizedType paramType = (ParameterizedType) field.getGenericType();
        Class<?> elementType = (Class<?>) paramType.getActualTypeArguments()[0];
        Collection collection = null;
        if (List.class.isAssignableFrom(field.getType())) {
            collection = instances.values().stream()
                    .filter(c -> elementType.isAssignableFrom(c.getClass()))
                    .collect(Collectors.toList());
        } else if (Set.class.isAssignableFrom(field.getType())) {
            collection = instances.values().stream()
                    .filter(c -> elementType.isAssignableFrom(c.getClass()))
                    .collect(Collectors.toSet());
        }

        return collection;
    }

    private static void injectDependency(Object instance, Field field, Object dependency) {
        if (dependency != null) {
            field.setAccessible(true);
            try {
                System.out.println(dependency + " injected in " + instance);
                field.set(instance, dependency);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to inject " + field.getName(), e);
            }
        } else {
            System.err.println("Warning: No dependency found for " + field.getType().getName()
                    + " in " + instance.getClass().getName());
        }
    }

    public void callPostConstruct() {
        for (Object instance : instances.values()) {
            invokePostConstruct(instance);
        }
    }

    private void invokePostConstruct(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.setAccessible(true);
                try {
                    if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Context.class) {
                        method.invoke(instance, this);
                    } else if (method.getParameterCount() == 0) {
                        method.invoke(instance);
                    } else {
                        throw new RuntimeException("@PostConstruct method must have 0 params or 1 param of type Context");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to call @PostConstruct on " + instance.getClass().getName(), e);
                }
            }
        }
    }

    // Для отладки
    public void printRegistry() {
        System.out.println("=== Registered instances ===");
        instances.keySet().forEach(k -> System.out.println("  " + k.getName()));
        System.out.println("=== Interface mappings ===");
        interfaceToImpl.forEach((iface, impl) -> System.out.println("  " + iface.getName() + " -> " + impl.getName()));
    }
}
