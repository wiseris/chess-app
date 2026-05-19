package com.nargiz.chess.shared.events.impl;

import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.events.GameEvent;
import com.nargiz.chess.shared.ioc.anotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class ApplicationEventBusImpl implements ApplicationEventBus {

    Map<Class<? extends GameEvent>, List<Consumer<? extends GameEvent>>> subscribtionMap = new ConcurrentHashMap<>();
    Map<Consumer<? extends GameEvent>, Class<? extends GameEvent>> unsubscribtionMap = new ConcurrentHashMap<>();

    @Override
    public <T extends GameEvent> void subscribeOn(Class<T> eventType, Consumer<T> eventProcess) {
        subscribtionMap
                .computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(eventProcess);
        unsubscribtionMap.put(eventProcess, eventType);
    }

    @Override
    public <T extends GameEvent> void unsubscribe(Consumer<T> eventProcess) {
        Class<? extends GameEvent> eventType = unsubscribtionMap.get(eventProcess);
        if (eventType != null) {
            List<Consumer<? extends GameEvent>> consumers = subscribtionMap.get(eventType);
            if (consumers != null) {
                consumers.remove(eventProcess);
            }
            unsubscribtionMap.remove(eventProcess);
        }
    }

    @Override
    public <T extends GameEvent> void publish(T event) {
        List<Consumer<? extends GameEvent>> consumers = subscribtionMap.get(event.getClass());
        if (consumers != null) {
            for (Consumer<? extends GameEvent> consumer : consumers) {
                ((Consumer<T>) consumer).accept(event);
            }
        }
    }
}
