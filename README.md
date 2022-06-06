## Картинка соответствующая изменению курса заданной валюты
REST cервис обращается к сервису курсов валют, и отображает gif:<br>
- если текущий курс на сегодня по отношению к USD стал выше вчерашнего, то сервис выдает рандомную картинку
отсюда: https://giphy.com/search/rich.
<br>
- если ниже или равен, то отсюда: https://giphy.com/search/broke.
<br>
Ссылки:<br>
- REST API курсов валют - https://docs.openexchangerates.org/
<br>
- REST API гифок - https://developers.giphy.com/docs/api#quick-start-guide
<br>

Сервис на Spring Boot 2.6.9 + Java.<br>
Запросы приходят на HTTP endpoint `/picture`. Код валюты, по отношению к которой сравнивается курс USD задается в 
параметре запроса `symbol`, например: `http://host/picture&symbol=EUR`
.<br>
Для взаимодействия с внешними сервисами используется Feign.<br>
Все параметры (валюта по отношению к которой смотрится курс, адреса внешних сервисов и т.д.) вынесены в `application.properties`
.<br>настройки
• На сервис написаны тесты (для мока внешних сервисов можно использовать @mockbean или WireMock)
• Для сборки должен использоваться Gradle
• Результатом выполнения должен быть репо на GitHub с инструкцией по запуску
Nice to Have
• Сборка и запуск Docker контейнера с этим сервисом<br>
Usage: `{baseUrl}/picture?symbol=<SYM>`.<br>
Example: `http://host/picture?symbol=EUR`.
<br>

If the rate has grown, the response picture will be of certain type, if the rate has fallen, then picture will of another different from picture type returned  
Application exposes `/picture` endpoint with named query parameter `symbol`. Репозиторий содержит выполнение курсового проекта "Облачное хранилище” по курсу "Java разработчик" в netology.ru.
## Постановка задачи
Требования к заданию изложены [здесь](https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md)
## Описание архитектуры решения

Описание в процессе