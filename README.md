# Задание

## Задача 0
### Сделать “калькулятор” на основе гугл-таблицы (или иным удобным способом), который по входным данным будет выводить колоду в более читаемом (на усмотрение выполняющего) виде. Калькулятор должен работать с любыми входными данными, колоды из входных данных даны в качестве примера

## Задача 1
### Добавить вывод параметров персонажей:
- Редкость (rarity)
- Раса (race)
- Класс (class)
- Пол (gender)
- Майт (power)
- Арт (image)
- Тип получения персонажа - ивентовый или нет (is_event)
- Тип персонажа - Варлорд или персонаж в руке

## Задача 2
Добавить вывод статов (атака и здоровье) и скиллов персонажей в руке, исходя из промоута и реборна

## Задача 3
Локализовать имена персонажей

## Задача 4
Локализовать скиллы

## Задача 5
Добавить вывод статов (атака и здоровье) и скиллов Варлорда, исходя из промоута

# _________________________

## Программа выполнена в виде простейшего Android приложения
APK файл будет приложен к проекту и к сообщению

# Как реализована работа программы:

## Задача 0
### Выбрав Excel файл с входными, он импортируется в SQLite БД, где каждая страница файла Excel - одна таблица. Вывод данных производится простым SQL запросом

## Задача 1
### После нажатия на кнопку "Отобразить колоду" выполняется запрос по таблице "колоды" с вставкой таблицы "конфигурация_персонажей"

## Задача 3
### Затем идет цикл по результатам этого запроса со следующим алгоритмом:
### Если количество промоутов карты в колоде > чем количество значений в столбце "promote_value" -> пропускаем одну строку inner_id и количество промоутов карты в колоде = количество промоутов карты в колоде - количество значений в столбце "promote_value".
### Если количество промоутов карты = 0 (то есть 1), тогда сразу идем к следующей карте в колоде.
### Если количество промоутов карты > 1 (то есть 2), тогда считаем attack, hp и skills по правилам из ТЗ.
### Выводим высчитанные значения карты в колоде и переходим в следующей карте в колоде, пока колода не закончится

## Из Заданий выполнены Задача 0, Задача 1 и Задача 2 (Без учитывания реборнов)
