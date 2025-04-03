# Комбинированная система анализа и подбора паролей

## Оглавление
- [Введение](#введение)
- [Архитектура системы](#архитектура-системы)
- [Подробное описание функционала](#подробное-описание-функционала)
- [Технические аспекты реализации](#технические-аспекты-реализации)
- [Руководство пользователя](#руководство-пользователя)
- [Руководство администратора](#руководство-администратора)
- [Безопасность и ограничения](#безопасность-и-ограничения)
- [Производительность](#производительность)
- [Дополнительные возможности](#дополнительные-возможности)
- [Часто задаваемые вопросы](#часто-задаваемые-вопросы)
- [Разработка и контрибуция](#разработка-и-контрибуция)

## Введение
### Назначение системы
Комбинированная система анализа и подбора паролей представляет собой учебно-демонстрационное приложение, разработанное для:
- Демонстрации принципов криптостойкости паролей
- Анализа времени, необходимого для взлома паролей методом перебора
- Исследования эффективности различных методов подбора паролей
- Обучения основам информационной безопасности

### Основные концепции
Система реализует два режима:
1. **Аналитический режим** - оценивает стойкость пароля к атакам методом полного перебора.
2. **Практический режим** - демонстрирует методы подбора паролей на тестовой системе аутентификации.

## Архитектура системы
### Компонентная диаграмма
```
+-----------------------+
|     GUI Layer         |
| (CombinedPasswordSystem) |
+-----------------------+
           |
           v
+-----------------------+
|   Business Logic      |
| 1. AuthenticationSystem |
| 2. PasswordStrengthAnalyzer |
| 3. PasswordCracker    |
+-----------------------+
           |
           v
+-----------------------+
|   Data Layer          |
| 1. User/Admin models  |
| 2. Dictionary files   |
| 3. Configuration      |
+-----------------------+
```

## Подробное описание функционала
### Система аутентификации
- **Обычные пользователи** могут менять пароль, но подвержены ограничениям политики безопасности.
- **Администраторы** имеют полный доступ к управлению пользователями и анализу паролей.

### Парольные политики
- Минимальная/максимальная длина пароля
- Минимальное количество цифр и спецсимволов
- Обязательное использование букв разного регистра

### Анализ надежности паролей
Используется следующая формула энтропии:
```math
Энтропия = log2(N^L)
```
Где `N` - мощность алфавита, а `L` - длина пароля.

## Технические аспекты реализации
### Ключевые технологии
- **Java SE 8+** – основная платформа
- **Swing** – графический интерфейс
- **BigInteger/BigDecimal** – работа с большими числами
- **Регулярные выражения** – анализ сложности паролей

### Обработка больших чисел
```java
BigInteger M = BigInteger.valueOf(N).pow(passwordLength);
BigDecimal totalTime = new BigDecimal(M).divide(BigDecimal.valueOf(speed), 50, RoundingMode.HALF_UP);
```

## Руководство пользователя
### Типовые сценарии
#### Анализ пароля
1. Откройте вкладку "Анализ пароля".
2. Введите пароль для проверки.
3. Получите отчет о его стойкости.

#### Регистрация нового пользователя
1. Введите имя пользователя.
2. Нажмите "Зарегистрироваться".
3. Установите пароль при первом входе.

## Руководство администратора
### Настройка системы
- Минимальная длина пароля: **8+ символов**
- Максимальная длина пароля: **64 символа**
- Автоматическая блокировка после **3 неудачных попыток**

## Безопасность и ограничения
### Ограничения системы
- Учебная направленность – **не для реального использования**
- Хранение паролей в **открытом виде** (не соответствует стандартам)
- Отсутствие шифрования конфиденциальных данных

## Производительность
### Факторы влияния
- Скорость подбора паролей: **100-10000 попыток/сек**
- Эффективность словарных атак: **до 30% для простых паролей**

## Дополнительные возможности
- **Интеграция с REST API** *(в разработке)*
- **Поддержка LDAP** *(планируется)*
- **Экспорт отчетов в PDF/CSV**

## Часто задаваемые вопросы
### Как увеличить эффективность подбора паролей?
- Добавьте больше слов в словарь
- Используйте специализированные словари
- Настройте параметры перебора

### Как сбросить пароль администратора?
Удалите `users.txt` и перезапустите систему – будет создан новый администратор с пустым паролем.

## Разработка и контрибуция
### Сборка из исходников
```bash
git clone https://github.com/your-repo/your-project.git
cd your-project
mvn clean package
```

### Дорожная карта
- Базовая система аутентификации
- Анализ стойкости паролей
- Методы подбора паролей
- Ведение журнала событий
- Шифрование хранимых данных

## Лицензия
Проект распространяется под лицензией **MIT**. Полный текст доступен в файле [LICENSE](LICENSE).

## UML-диаграмма классов
```plantuml
@startuml

class CombinedPasswordSystem {
  -authSystem: AuthenticationSystem
  -strengthAnalyzer: PasswordStrengthAnalyzer
  -passwordCracker: PasswordCracker
  +main(args: String[]): void
}

class AuthenticationSystem {
  -users: List<User>
  -dataFile: String
  +addUser(username: String): void
  +blockUser(username: String): void
  +findUser(username: String): User
  +saveUsers(): void
  +loadUsers(): List<User>
}

class PasswordStrengthAnalyzer {
  +analyzePassword(password: String, speed: double, attempts: int, pause: double): String
  -calculateAlphabetSize(password: String): int
  -calculateCombinations(N: int, L: int): BigInteger
  -estimateCrackingTime(M: BigInteger, speed: double, m: int, v: double): String
}

class PasswordCracker {
  -authSystem: AuthenticationSystem
  -russianToLatinMap: Map<Character, Character>
  -dictionary: List<String>
  +dictionaryAttack(username: String): String
  +bruteForceAttack(username: String, maxLength: int): String
  +combinedAttack(username: String, maxLength: int): String
}

class User {
  -username: String
  -password: String
  -isBlocked: boolean
}

CombinedPasswordSystem --> AuthenticationSystem
CombinedPasswordSystem --> PasswordStrengthAnalyzer
CombinedPasswordSystem --> PasswordCracker

AuthenticationSystem --> User

@enduml
# Комбинированная система анализа и подбора паролей - Полная документация

## Оглавление
- [Введение](#введение)
- [Архитектура системы](#архитектура-системы)
- [Подробное описание функционала](#подробное-описание-функционала)
- [Технические аспекты реализации](#технические-аспекты-реализации)
- [Руководство пользователя](#руководство-пользователя)
- [Руководство администратора](#руководство-администратора)
- [Безопасность и ограничения](#безопасность-и-ограничения)
- [Производительность](#производительность)
- [Дополнительные возможности](#дополнительные-возможности)
- [Часто задаваемые вопросы](#часто-задаваемые-вопросы)
- [Разработка и контрибуция](#разработка-и-контрибуция)

---

## Введение
### Назначение системы
Комбинированная система анализа и подбора паролей представляет собой учебно-демонстрационное приложение, разработанное для:

- Демонстрации принципов криптостойкости паролей
- Анализа времени, необходимого для взлома паролей методом перебора
- Исследования эффективности различных методов подбора паролей
- Обучения основам информационной безопасности

### Основные концепции
Система реализует два взаимодополняющих режима:
- **Аналитический режим** — оценивает стойкость пароля к атакам методом полного перебора
- **Практический режим** — демонстрирует реальные методы подбора паролей на тестовой системе аутентификации

## Архитектура системы
### Компонентная диаграмма
```plaintext
+-----------------------+
|     GUI Layer        |
| (CombinedPasswordSystem) |
+-----------------------+
           |
           v
+-----------------------+
|   Business Logic     |
| 1. AuthenticationSystem |
| 2. PasswordStrengthAnalyzer |
| 3. PasswordCracker    |
+-----------------------+
           |
           v
+-----------------------+
|   Data Layer         |
| 1. User/Admin models |
| 2. Dictionary files  |
| 3. Configuration     |
+-----------------------+
```

## UML-диаграммы
### 1. Диаграмма классов
```plantuml
@startuml
class CombinedPasswordSystem {
  -authSystem: AuthenticationSystem
  -strengthAnalyzer: PasswordStrengthAnalyzer
  -passwordCracker: PasswordCracker
  +main(args: String[]): void
}

class AuthenticationSystem {
  -users: List<User>
  -dataFile: String
  +addUser(username: String): void
  +blockUser(username: String): void
  +findUser(username: String): User
  +saveUsers(): void
  +loadUsers(): List<User>
}

class PasswordStrengthAnalyzer {
  +analyzePassword(password: String, speed: double, attempts: int, pause: double): String
  -calculateAlphabetSize(password: String): int
  -calculateCombinations(N: int, L: int): BigInteger
  -estimateCrackingTime(M: BigInteger, speed: double, m: int, v: double): String
}

class PasswordCracker {
  -authSystem: AuthenticationSystem
  -russianToLatinMap: Map<Character, Character>
  -dictionary: List<String>
  +dictionaryAttack(username: String): String
  +bruteForceAttack(username: String, maxLength: int): String
  +combinedAttack(username: String, maxLength: int): String
  -convertRussianToLatin(russianWord: String): String
  -incrementChars(chars: char[]): boolean
}

class User {
  -username: String
  -password: String
  -isBlocked: boolean
  -passwordRestrictions: boolean
  -failedAttempts: int
  -minLength: int
  -maxLength: int
  -minDigits: int
  -minSpecials: int
  +login(password: String): boolean
  +isPasswordValid(password: String): boolean
}

class Admin {
  +toString(): String
}

CombinedPasswordSystem --> AuthenticationSystem
CombinedPasswordSystem --> PasswordStrengthAnalyzer
CombinedPasswordSystem --> PasswordCracker
AuthenticationSystem --> User
User <|-- Admin
PasswordCracker --> AuthenticationSystem
@enduml
```

### 2. Диаграмма последовательностей - Анализ пароля
```plantuml
@startuml
actor User
participant GUI
participant PasswordStrengthAnalyzer
participant "BigInteger/BigDecimal" as Math

User -> GUI: Вводит пароль и параметры
GUI -> PasswordStrengthAnalyzer: analyzePassword(password, speed, attempts, pause)
PasswordStrengthAnalyzer -> PasswordStrengthAnalyzer: calculateAlphabetSize()
PasswordStrengthAnalyzer -> PasswordStrengthAnalyzer: calculateCombinations()
PasswordStrengthAnalyzer -> Math: Вычисления
Math --> PasswordStrengthAnalyzer: Результаты
PasswordStrengthAnalyzer -> PasswordStrengthAnalyzer: estimateCrackingTime()
PasswordStrengthAnalyzer --> GUI: Отчет о стойкости
GUI --> User: Отображает результаты
@enduml
```

### 3. Диаграмма вариантов использования
```plantuml
@startuml
left to right direction
actor Пользователь
actor Администратор

rectangle Система {
  Пользователь --> (Анализ стойкости пароля)
  Пользователь --> (Регистрация)
  Пользователь --> (Смена пароля)
  
  Администратор --> (Управление пользователями)
  Администратор --> (Настройка политик)
  Администратор --> (Подбор паролей)
  Администратор --> (Просмотр статистики)
}

(Управление пользователями) .> (Блокировка пользователей) : includes
(Управление пользователями) .> (Просмотр списка) : includes
@enduml
```

### 4. Диаграмма состояний - Пользователь
```plantuml
@startuml
[*] --> Неактивный
Неактивный --> Зарегистрированный: Регистрация
Зарегистрированный --> Аутентифицированный: Успешный вход
Аутентифицированный --> Зарегистрированный: Выход
Зарегистрированный --> Заблокированный: 3 неудачных попытки
Заблокированный --> [*] : Сброс администратором
@enduml
```

### 5. Диаграмма компонентов
```plantuml
@startuml
package "Комбинированная система паролей" {
  [GUI] as gui
  [Authentication Module] as auth
  [Password Analysis] as analysis
  [Password Cracking] as crack
  [Data Storage] as data
}

gui --> auth
gui --> analysis
gui --> crack
auth --> data
analysis --> data
crack --> auth
crack --> data
@enduml
```

![Снимок экрана 2025-04-03 190124](https://github.com/user-attachments/assets/c51dccaf-966b-4b9d-8979-d8c6dea8d606)

![Editor _ Mermaid Chart-2025-04-03-135930](https://github.com/user-attachments/assets/5bc3602d-a379-4b80-8c0e-e4028935de18)


