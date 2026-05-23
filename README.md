# Chess App

Сетевая шахматная игра

## Модули

- `server` — серверная часть
- `shared` — общие классы, модели, команды и др
- `client` — клиент на JavaFX

### Сервер

`server/ServerLauncher.java` — точка запуска, поднимает TCP-сервер на порту 8888

`server/network/impl/TCPServerImpl.java` — принимает подключения, хранит клиентов, рассылает broadcast, при отключении игрока шлёт ErrorResponse оставшимся

`server/network/impl/ClientHandlerImpl.java` — читает JSON из сокета, парсит Envelope, вызывает процессор команд, отправляет ответ

`server/network/impl/DiscoveryResponderImpl.java` — отвечает на UDP-запросы клиентов, сообщает список лобби

`server/process/impl/ConnectServerProcessor.java` — обработка подключения
`server/process/impl/CreateLobbyProcessor.java` — обработка создания лобби
`server/process/impl/JoinLobbyProcessor.java` — обработка входа в лобби
`server/process/impl/StartGameProcessor.java` — обработка запуска игры
`server/process/impl/ActionCommandProcessor.java` — обработка хода

`server/service/impl/GameServiceImpl.java` — вся бизнес-логика: создание лобби, вход, старт игры, выполнение хода, проверка валидации, рассылка обновлений, обработка отключения

`server/repository/impl/ServerRepositoryImpl.java` — хранение лобби, участников, игр, фигур, истории в ConcurrentHashMap

`server/events/MemberDisconnectedEvent.java` — событие отключения игрока

### Клиент

`client/Launcher.java` — точка запуска JavaFX, создание DI-контекста, обработка закрытия окна

`client/network/impl/TCPClientImpl.java` — подключение к серверу, отправка JSON, приём ответов, передача процессорам

`client/services/impl/ServerDiscoveryImpl.java` — UDP broadcast для поиска серверов в сети

`client/process/impl/ErrorResponseProcessor.java` — показывает Alert с ошибкой/уведомлением
`client/process/impl/StartGameResponseProcessor.java` — публикует StartGameEvent
`client/process/impl/UpdateGameStateProcessor.java` — публикует UpdateGameStateEvent
`client/process/impl/UpdateMembersProcessor.java` — публикует UpdateMembersEvent
`client/process/impl/CreateLobbyResponseProcessor.java` — публикует LobbyCreatedEvent
`client/process/impl/JoinLobbyResponseProcessor.java` — публикует JoinLobbyEvent
`client/process/impl/ConnectServerResponseProcessor.java` — логирует подключение

`client/ui/screens/MainScreenController.java` — главное меню
`client/ui/screens/ServerListController.java` — список серверов
`client/ui/screens/HostLobbyController.java` — лобби (создатель)
`client/ui/screens/SeekerLobbyController.java` — лобби (гость)
`client/ui/screens/GameController.java` — игровая доска, перетаскивание фигур, история, кнопка выхода

`client/ui/dialogs/DialogCreateLobbyController.java` — создание лобби
`client/ui/dialogs/DialogJoinLobbyController.java` — вход в лобби
`client/ui/dialogs/DialogSelectTransformationController.java` — выбор фигуры при превращении пешки

`client/model/Figure.java` — модель фигуры для отображения (связь FigureData с ImageView)
`client/model/events/StartGameEvent.java` — событие начала игры
`client/model/events/UpdateGameStateEvent.java` — событие обновления доски
`client/model/events/UpdateMembersEvent.java` — событие обновления участников
`client/model/events/ErrorEvent.java` — событие ошибки
`client/model/events/ServerFoundEvent.java` — событие найденного сервера

### Shared

`shared/command/Envelope.java` — обёртка JSON-сообщений (имя команды + данные)
`shared/command/ChessCommand.java` — базовый класс всех команд и ответов
`shared/command/ActionCommand.java` — ход фигурой (from, to, превращение)
`shared/command/CreateLobby.java` — создание лобби
`shared/command/JoinLobby.java` — вход в лобби
`shared/command/StartGame.java` — запуск игры
`shared/command/UpdateGameState.java` — обновление состояния доски
`shared/command/UpdateMembers.java` — обновление состава лобби
`shared/command/response/ErrorResponse.java` — сообщение об ошибке
`shared/command/response/StartGameResponse.java` — кто каким цветом играет, фигуры

`shared/fabric/impl/ChessCommandFabricImpl.java` — по строке имени команды находит её класс

`shared/events/impl/ApplicationEventBusImpl.java` — шина событий (подписка, публикация)
`shared/events/ApplicationStopEvent.java` — событие закрытия приложения

`shared/ioc/Container.java` — сканирует пакет, находит @Component, создаёт бины
`shared/ioc/Context.java` — singleton-хранилище бинов, внедрение @Inject, вызов @PostConstruct

`shared/models/GameData.java` — состояние игры: белый, чёрный, фигуры, история, GameState
`shared/models/FigureData.java` — базовая фигура: позиция, цвет, тип, validateAction(), isUnderAttack()
`shared/models/CellPosition.java` — клетка доски: row, column, нотация, проверки диагоналей
`shared/models/HistoryData.java` — запись хода: from/to, шах, мат, рокировка, нотация
`shared/models/LobbyData.java` — лобби: имя, хост, участники, игра
`shared/models/MemberData.java` — игрок: id, имя, статус, король
`shared/models/ActionResult.java` — результат валидации хода (ошибки + действия)
`shared/models/ServerInfo.java` — данные сервера для отображения

`shared/models/figures/KingData.java` — король: ход на 1 клетку, рокировка
`shared/models/figures/QueenData.java` — ферзь: вертикаль, горизонталь, диагональ
`shared/models/figures/RookData.java` — ладья: вертикаль, горизонталь
`shared/models/figures/BishopData.java` — слон: диагональ
`shared/models/figures/KnightData.java` — конь: буквой Г, прыгает через фигуры
`shared/models/figures/PawnData.java` — пешка: вперёд, атака по диагонали, en passant, превращение

`shared/models/actions/MoveFigure.java` — переместить фигуру
`shared/models/actions/KillFigure.java` — убрать фигуру с доски
`shared/models/actions/TransformFigure.java` — превратить пешку

`shared/models/enums/ColorType.java` — WHITE, BLACK
`shared/models/enums/FigureType.java` — KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
`shared/models/enums/GameState.java` — PLAYING, DRAW, WHITE_WINS, BLACK_WINS

`shared/constants/NetworkConstants.java` — порты, DISCOVERY_QUERY
