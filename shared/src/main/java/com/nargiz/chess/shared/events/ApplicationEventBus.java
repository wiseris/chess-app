package com.nargiz.chess.shared.events;

import java.util.function.Consumer;

public interface ApplicationEventBus {
    <T extends GameEvent> void subscribeOn(Class<T> eventType, Consumer<T> eventProcess);
    <T extends GameEvent> void unsubscribe(Consumer<T> eventProcess);

    <T extends GameEvent> void publish(T event);
}
