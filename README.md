# Chess Project README

## Описание проекта
Проект разделён на несколько основных модулей:

- `server` — серверная часть
- `shared` — общие классы, модели, команды и инфраструктура
- `client` — клиентская часть

Основная идея архитектуры — разделение логики игры, сетевого транспорта и инфраструктуры приложения.

---

## 2. Архитектура проекта

Проект разделён на 2 ключевых слоя:

### 2.1 Server (`server`)
Отвечает за:

- обработку сетевых подключений
- управление лобби
- управление игровыми сессиями
- выполнение команд
- синхронизацию состояния игры

---

### 2.2 Shared (`shared`)
Содержит всю бизнес-логику и используется как на сервере, так и на клиенте:

- модели игры
- шахматная логика
- команды
- события
- IoC контейнер
- утилиты

---

## 3. Сетевое взаимодействие

Система построена вокруг **Command-based protocol**.

### Поток данных:

1. Клиент отправляет `ChessCommand`
2. Сервер десериализует команду через `ChessCommandFabric`
3. Команда передаётся в `GameService`
4. Выполняется логика игры
5. Сервер возвращает `ChessCommandResponse`
6. Клиенты получают `UpdateGameState`

---

## 4. Командная система

### 4.1 Базовый класс

`ChessCommand`

Содержит:

- `commandId` — уникальный ID команды
- `userId` — отправитель

---

### 4.2 Игровые команды

#### CreateLobby
Создание игрового лобби.

#### JoinLobby
Подключение к существующему лобби.

#### ConnectServer
Первичное подключение клиента.

#### StartGame
Запуск партии.

#### ActionCommand
Основная игровая команда (ход фигуры).

Содержит:
- fromPosition
- toPosition
- transform (превращение пешки)

#### UpdateGameState
Синхронизация состояния игры.

#### UpdateMembers
Обновление игроков в лобби.

---

## 5. Ответы сервера

Все ответы наследуются от `ChessCommandResponse`.

### Основные:

- `ConnectServerResponse`
- `CreateLobbyResponse`
- `JoinLobbyResponse`
- `StartGameResponse`
- `ErrorResponse`

---

## 6. Envelope

`Envelope` — универсальная обёртка сообщений:

- `command` — имя команды
- `body` — payload

Используется для сетевой сериализации.

---

## 7. IoC контейнер

Реализован вручную.

### 7.1 Основные возможности:

- создание singleton компонентов
- dependency injection
- post construct lifecycle
- регистрация интерфейсов
- автоматический scan пакетов

---

### 7.2 Аннотации:

- `@Component` — компонент контейнера
- `@Inject` — внедрение зависимостей
- `@PostConstruct` — инициализация после DI
- `@Command` — сетевые команды

---

### 7.3 Context

Контейнер хранит:

- instances (объекты)
- interface mappings

Поддерживает:

- get(Class)
- injectDependencies()
- callPostConstruct()

---

## 8. Event Bus

### 8.1 ApplicationEventBus

Поддерживает:

- subscribeOn(event)
- unsubscribe()
- publish()

---

### 8.2 Особенности реализации:

- ConcurrentHashMap
- Consumer-based подписки
- типизированные события

---

## 9. Фабрика команд

`ChessCommandFabric`

### Назначение:

- автоматический поиск команд
- регистрация всех `@Command`
- маппинг String → Class

---

## 10. Игровая модель

---

## 10.1 GameData

Главная сущность игры:

- white / black игроки
- текущий state
- карта фигур
- история ходов

---

## 10.2 FigureData

Базовый класс всех фигур:

Содержит:

- position
- color
- type
- moved
- dead

Методы:

- validateAction()
- isUnderAttack()
- isLocked()

---

## 10.3 CellPosition

Клетка шахматной доски.

Поддерживает:

- shift
- проверки row/column/diagonal
- шахматную нотацию
- проверку выхода за доску

---

## 10.4 HistoryData

Хранит информацию о ходе:

- from/to
- фигура
- тип действия
- шах / мат
- рокировка
- en passant

---

## 10.5 LobbyData

Лобби содержит:

- host
- members
- game

---

## 10.6 MemberData

Игрок:

- id
- name
- status
- king reference

---

## 11. Шахматные фигуры

Каждая фигура реализует свою логику.

---

### 11.1 Общий принцип

Каждая фигура:

1. Проверяет валидность хода
2. Проверяет шах короля
3. Проверяет препятствия
4. Формирует ActionResult

---

### 11.2 Реализованные фигуры

#### King
- обычные ходы
- castling (рокировка)
- защита от шаха

#### Queen
- диагональ + линия

#### Rook
- линии (row/column)

#### Bishop
- диагонали

#### Knight
- L-ход

#### Pawn
- движение вперёд
- двойной ход
- взятие
- en passant
- превращение

---

## 12. Action System

Используется паттерн Command внутри игры.

### 12.1 ActionPerform

```java
void doAction(GameData gameData)
