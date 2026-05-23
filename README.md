# Chess App

## Модули

- `server` — серверная часть
- `shared` — общие классы, модели, команды и др
- `client` — клиент на JavaFX

## Главные файлы

`shared/ioc/Container.java` — сканирование пакета и создание всех объектов

`client/network/impl/TCPClientImpl.java` — TCP-сокет, подключение к серверу, отправка и приём JSON

`server/network/impl/TCPServerImpl.java` — ServerSocket, приём TCP-подключений, broadcast, отключения

`server/network/impl/ClientHandlerImpl.java` — чтение JSON из сокета, парсинг, вызов обработчиков

`server/network/impl/DiscoveryResponderImpl.java` — UDP-сокет, ответ на поиск серверов

`client/services/impl/ServerDiscoveryImpl.java` — UDP-broadcast, поиск серверов в сети

`shared/command/Envelope.java` — обёртка сообщений: имя команды + данные

`shared/events/impl/ApplicationEventBusImpl.java` — шина событий

`server/service/impl/GameServiceImpl.java` — лобби, игра, ходы, валидация, отключения

`server/repository/impl/ServerRepositoryImpl.java` — хранение всех данных в памяти

`shared/models/FigureData.java` — фигура, проверка хода и атаки

`shared/models/figures/PawnData.java` — пешка: ход, атака, en passant, превращение

`shared/models/figures/KingData.java` — король: ход, рокировка

`client/ui/screens/GameController.java` — игровая доска и drag-and-drop

`client/process/impl/ErrorResponseProcessor.java` — показ уведомлений
