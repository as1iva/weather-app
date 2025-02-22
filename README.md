# ☀️☁️ Проект "Погода"
## Описание
Веб-приложение для просмотра текущей погоды. Пользователь может зарегистрироваться и добавить в коллекцию одну или несколько локаций (городов, сёл, других пунктов), после чего главная страница приложения начинает отображать список локаций с их текущей погодой.

ТЗ проекта – [здесь](https://zhukovsd.github.io/java-backend-learning-course/projects/weather-viewer/)

## Использованные технологии / инструменты
### Backend
- Spring MVC
- Hibernate
- PostgreSQL
- Apache Maven
- Apache Tomcat 10
- Lombok
- OpenWeatherAPI 

### Testing
- JUnit 5
- Mockito
- H2

### Frontend
- Thymeleaf
- HTML
- CSS

### Deploy
- Docker

## Функционал

### Главная страница
Адрес - `/`
- Просмотр погоды сохранненых локаций
- Поиск локаций
- Выход из аккаунта

### Страница поиска локаций
Адрес - `/search`
- Поиск локаций
- Добавление локаций на главную страницу

### Авторизации
Адрес – `/login`
- Авторизация пользователя по существующему логину и паролю
  
### Регистрация
Адрес – `/signup`
- Регистрация пользователя по уникальному логику и паролю
- Подтверждение пароля для успешной регистрации

## Зависимости
- Java 17+
- Apache Maven
- Tomcat 10
- Docker
- OpenWeatherAPI key

## Установка проекта

1. Склонируйте репозиторий:
```
git@github.com:as1iva/weather-app.git
```
2. Откройте `IntelliJ IDEA`

3. В главном меню нажмите `Open` и выберите папку, которую склонировали в первом шаге

4. В консоли `IntelliJ IDEA` пропишите команду:
```
docker-compose up -d
```
5. Далее, выберите базу данных PostgreSQL и заполните данные:
```
User: postgres
Password: macbook
```
6. В `application.properties` заполните поле `api.key` (Получить ключ можно на сайте [OpenWeather](https://openweathermap.org/))

7. В `IntelliJ IDEA` выберите меню `Run`, а затем `Edit Configurations...`

8. В появившемся окне нажмите `+` и добавьте `Tomcat Server` -> `local`

9. Рядом с предупреждением `Warning: No artifacts marked for deployment` нажмите на кнопку `Fix`

10. В появившемся окне выберите `weather-app:war exploded`

11. В поле `Application context` оставьте лишь `/`

12. Нажмите `Apply` и запустите `Tomcat`

13. Теперь можно открыть http://localhost:8080/ и попробовать функционал приложения
