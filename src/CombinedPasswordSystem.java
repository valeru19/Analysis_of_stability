import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

// Остальные классы (User, Admin, AuthenticationSystem) остаются без изменений
// Класс User представляет обычного пользователя.
class User {
    private String username; // Имя пользователя
    private String password; // Пароль пользователя
    private boolean isBlocked; // Флаг блокировки пользователя
    private boolean passwordRestrictionsEnabled; // Флаг ограничений на пароль
    private int failedLoginAttempts; // Счетчик неудачных попыток ввода пароля

    // Поля для индивидуальных ограничений
    private int minPasswordLength = 6;  // Минимальная длина пароля
    private int maxPasswordLength = 12; // Максимальная длина пароля
    private int minDigit = 2;   // Минимально кол-во цифр в пароле
    private int minSpecialChars = 2;    // Минимальное кол-во специальных символов в пароле

    // Конструктор для создания объекта пользователя.
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isBlocked = false; // По умолчанию пользователь не заблокирован
        this.passwordRestrictionsEnabled = true; // Ограничения на пароль включены по умолчанию
        this.failedLoginAttempts = 0; // Инициализация счетчика неудачных попыток
    }

    // Геттеры и сеттеры для ограничений
    public int getMinPasswordLength(){return minPasswordLength;}
    public void setMinPasswordLength(int minPasswordLength){this.minPasswordLength = minPasswordLength;}
    public int getMaxPasswordLength(){return maxPasswordLength;}
    public void setMaxPasswordLength(int maxPasswordLength) {this.maxPasswordLength = maxPasswordLength;}
    public int getMinDigit(){return minDigit;}
    public void setMinDigit(int minDigit) {this.minDigit = minDigit;}
    public int getMinSpecialChars(){return minSpecialChars;}
    public void setMinSpecialChars(int minSpecialChars) {this.minSpecialChars = minSpecialChars;}

    // Геттеры и сеттеры для основных полей
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public boolean isBlocked() {return isBlocked;}
    public void setBlocked(boolean blocked) {isBlocked = blocked;}
    public boolean isPasswordRestrictionsEnabled() {return passwordRestrictionsEnabled;}
    public void setPasswordRestrictionsEnabled(boolean passwordRestrictionsEnabled) {this.passwordRestrictionsEnabled = passwordRestrictionsEnabled;}
    public int getFailedLoginAttempts() {return failedLoginAttempts;}
    public void setFailedLoginAttempts(int failedLoginAttempts) {this.failedLoginAttempts = failedLoginAttempts;}

    // Метод для проверки пароля пользователя.
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    // Метод проверки пароля на соответствие индивидуальным ограничениям
    public boolean isPasswordValid(String password){
        //
        if(password.length() < minPasswordLength){
            return false;
        }
        //
        if(password.length() > maxPasswordLength){
            return false;
        }
        //
        int digitCount = 0;
        for(char c : password.toCharArray()){
            if(Character.isDigit(c)){
                digitCount++;
            }
        }
        if(digitCount < minDigit){
            return false;
        }
        int specialCharCount = 0;
        for(char c : password.toCharArray()){
            if(!Character.isLetterOrDigit(c)){
                specialCharCount++;
            }
        }
        if(specialCharCount < minSpecialChars){
            return false;
        }
        return true;
    }


    // Переопределенный метод toString для строкового представления пользователя.
    @Override
    public String toString() {
        return "Пользователь: " + username + " | Заблокирован: " + isBlocked + " | Ограничения на пароль: " + passwordRestrictionsEnabled;
    }
}

// Класс Admin представляет администратора.
class Admin extends User {
    // Конструктор для создания объекта администратора.
    public Admin(String username, String password) {
        super(username, password); // Вызов конструктора родительского класса
    }

    // Переопределенный метод toString для строкового представления администратора.
    @Override
    public String toString() {
        return "Администратор: " + getUsername();
    }
}

// Класс AuthenticationSystem управляет аутентификацией и хранением данных.
class AuthenticationSystem {
    private List<User> users; // Список пользователей
    private String dataFile;   // Имя файла для хранения данных

    // Конструктор для инициализации системы аутентификации.
    public AuthenticationSystem(String dataFile) {
        this.dataFile = dataFile;
        this.users = loadUsers(); // Загрузка пользователей из файла
        if (users.isEmpty()) {
            // При первом запуске создаем администратора с пустым паролем
            users.add(new Admin("admin", ""));
            saveUsers();
        }
    }

    // Метод для загрузки пользователей из файла.
    private List<User> loadUsers() {
        List<User> userList = new ArrayList<>(); // Создаем пустой список пользователей
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            // Читаем файл построчно
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Разделяем строку по запятой
                String username = parts[0];      // Имя пользователя
                String password = parts[1];      // Пароль
                boolean isBlocked = Boolean.parseBoolean(parts[2]); // Флаг блокировки
                boolean passwordRestrictionsEnabled = Boolean.parseBoolean(parts[3]); // Флаг ограничений на пароль
                // Создаем объект User или Admin в зависимости от роли
                User user = (username.equals("admin")) ? new Admin(username, password) : new User(username, password);
                user.setBlocked(isBlocked);
                user.setPasswordRestrictionsEnabled(passwordRestrictionsEnabled);
                userList.add(user);
            }
        } catch (IOException e) {
            System.out.println("Данные о пользователях не найдены. Начинаем с пустого списка.");
        }
        return userList;
    }

    // Метод для сохранения пользователей в файл.
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (User user : users) {
                // Записываем данные в файл в формате: username,password,isBlocked,passwordRestrictionsEnabled
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.isBlocked() + "," + user.isPasswordRestrictionsEnabled());
                writer.newLine(); // Переход на новую строку
            }
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных о пользователях.");
        }
    }

    // Метод для поиска пользователя по имени.
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Метод для добавления нового пользователя.
    public void addUser(String username) {
        if (findUser(username) != null) {
            JOptionPane.showMessageDialog(null, "Пользователь уже существует.");
            return;
        }
        users.add(new User(username, "")); // Добавляем пользователя с пустым паролем
        saveUsers(); // Сохраняем изменения в файл
        JOptionPane.showMessageDialog(null, "Пользователь " + username + " успешно добавлен.");
    }

    // Метод для блокировки пользователя.
    public void blockUser(String username) {
        User user = findUser(username);
        if (user != null) {
            user.setBlocked(true); // Блокируем пользователя
            saveUsers(); // Сохраняем изменения в файл
            JOptionPane.showMessageDialog(null, "Пользователь " + username + " успешно заблокирован.");
        } else {
            JOptionPane.showMessageDialog(null, "Пользователь не найден.");
        }
    }

    // Метод для включения/отключения ограничений на пароли.
    public void togglePasswordRestrictions(boolean enabled) {
        for (User user : users) {
            user.setPasswordRestrictionsEnabled(enabled); // Устанавливаем ограничения для всех пользователей
        }
        saveUsers(); // Сохраняем изменения в файл
        JOptionPane.showMessageDialog(null, "Ограничения на пароли " + (enabled ? "включены" : "отключены") + " для всех пользователей.");
    }

    // Метод для получения списка всех пользователей.
    public List<User> getUsers() {
        return users;
    }
}

public class CombinedPasswordSystem extends JFrame {
    private AuthenticationSystem authSystem;
    private PasswordStrengthAnalyzer strengthAnalyzer;
    private PasswordCracker passwordCracker;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton analyzeButton;
    private JLabel messageLabel;

    public CombinedPasswordSystem() {
        authSystem = new AuthenticationSystem("users.txt");
        strengthAnalyzer = new PasswordStrengthAnalyzer();
        passwordCracker = new PasswordCracker(authSystem);

        setTitle("Комбинированная система паролей");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        panel.add(new JLabel("Имя пользователя:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Войти");
        panel.add(loginButton);

        registerButton = new JButton("Зарегистрироваться");
        panel.add(registerButton);

        analyzeButton = new JButton("Анализ пароля");
        panel.add(analyzeButton);

        messageLabel = new JLabel("");
        panel.add(messageLabel);

        add(panel);

        // Обработчики событий
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegistration());
        analyzeButton.addActionListener(e -> showPasswordAnalysisDialog());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authSystem.findUser(username);
        if (user == null) {
            messageLabel.setText("Пользователь не найден");
            return;
        }

        if (user.isBlocked()) {
            messageLabel.setText("Пользователь заблокирован");
            return;
        }

        if (user.login(password)) {
            messageLabel.setText("Вход выполнен успешно");
            if (user instanceof Admin) {
                openAdminMenu((Admin) user);
            } else {
                openUserMenu(user);
            }
        } else {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 3) {
                messageLabel.setText("Превышено количество попыток. Программа завершается");
                System.exit(0);
            }
            messageLabel.setText("Неверный пароль. Осталось попыток: " + (3 - user.getFailedLoginAttempts()));
        }
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        authSystem.addUser(username);
    }

    private void showPasswordAnalysisDialog() {
        JDialog analysisDialog = new JDialog(this, "Анализ надежности пароля", true);
        analysisDialog.setSize(600, 400);
        analysisDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField passwordField = new JTextField();
        JTextField speedField = new JTextField("1000");
        JTextField attemptsField = new JTextField("10");
        JTextField pauseField = new JTextField("5");

        inputPanel.add(new JLabel("Пароль для анализа:"));
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Скорость перебора (попыток/сек):"));
        inputPanel.add(speedField);
        inputPanel.add(new JLabel("Попыток перед паузой:"));
        inputPanel.add(attemptsField);
        inputPanel.add(new JLabel("Длительность паузы (сек):"));
        inputPanel.add(pauseField);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JButton analyzeButton = new JButton("Анализировать");
        analyzeButton.addActionListener(e -> {
            try {
                String password = passwordField.getText();
                double speed = Double.parseDouble(speedField.getText());
                int attempts = Integer.parseInt(attemptsField.getText());
                double pause = Double.parseDouble(pauseField.getText());

                String analysisResult = strengthAnalyzer.analyzePassword(password, speed, attempts, pause);
                resultArea.setText(analysisResult);
            } catch (NumberFormatException ex) {
                resultArea.setText("Ошибка: введите корректные числовые значения");
            }
        });

        analysisDialog.add(inputPanel, BorderLayout.NORTH);
        analysisDialog.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        analysisDialog.add(analyzeButton, BorderLayout.SOUTH);
        analysisDialog.setVisible(true);
    }

    private void openAdminMenu(Admin admin) {
        JFrame adminFrame = new JFrame("Меню администратора");
        adminFrame.setSize(600, 400);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));

        JButton changePasswordButton = new JButton("Сменить пароль");
        JButton viewUsersButton = new JButton("Просмотреть пользователей");
        JButton addUserButton = new JButton("Добавить пользователя");
        JButton blockUserButton = new JButton("Заблокировать пользователя");
        JButton toggleRestrictionsButton = new JButton("Включить/отключить ограничения на пароли");
        JButton setPasswordRulesButton = new JButton("Настроить ограничения на пароль");
        JButton crackPasswordButton = new JButton("Подобрать пароль ADMIN");

        panel.add(changePasswordButton);
        panel.add(viewUsersButton);
        panel.add(addUserButton);
        panel.add(blockUserButton);
        panel.add(toggleRestrictionsButton);
        panel.add(setPasswordRulesButton);
        panel.add(crackPasswordButton);

        adminFrame.add(panel);
        adminFrame.setVisible(true);

        // Обработчики событий
        changePasswordButton.addActionListener(e -> changePassword(admin));
        viewUsersButton.addActionListener(e -> viewUsers());
        addUserButton.addActionListener(e -> addUser());
        blockUserButton.addActionListener(e -> blockUser());
        toggleRestrictionsButton.addActionListener(e -> toggleRestrictions());
        setPasswordRulesButton.addActionListener(e -> setPasswordRules());
        crackPasswordButton.addActionListener(e -> crackAdminPassword());
    }

    private void crackAdminPassword() {
        JDialog crackDialog = new JDialog(this, "Подбор пароля ADMIN", true);
        crackDialog.setSize(500, 300);
        crackDialog.setLayout(new BorderLayout());

        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new GridLayout(3, 1));

        JButton dictionaryButton = new JButton("Метод словаря");
        JButton bruteForceButton = new JButton("Полный перебор");
        JButton combinedButton = new JButton("Комбинированный метод");

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        dictionaryButton.addActionListener(e -> {
            String result = passwordCracker.dictionaryAttack("admin");
            resultArea.setText(result);
        });

        bruteForceButton.addActionListener(e -> {
            String result = passwordCracker.bruteForceAttack("admin", 6);
            resultArea.setText(result);
        });

        combinedButton.addActionListener(e -> {
            String result = passwordCracker.combinedAttack("admin", 6);
            resultArea.setText(result);
        });

        methodPanel.add(dictionaryButton);
        methodPanel.add(bruteForceButton);
        methodPanel.add(combinedButton);

        crackDialog.add(methodPanel, BorderLayout.NORTH);
        crackDialog.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        crackDialog.setVisible(true);
    }

    // Остальные методы меню администратора и пользователя...
    private void openUserMenu(User user) {
        JFrame userFrame = new JFrame("Меню пользователя");
        userFrame.setSize(300, 200);
        userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton changePasswordButton = new JButton("Сменить пароль");
        JButton logoutButton = new JButton("Выйти");

        panel.add(changePasswordButton);
        panel.add(logoutButton);

        userFrame.add(panel);
        userFrame.setVisible(true);

        changePasswordButton.addActionListener(e -> changePassword(user));
        logoutButton.addActionListener(e -> userFrame.dispose());
    }

    private void changePassword(User user) {
        String oldPassword = JOptionPane.showInputDialog("Введите старый пароль:");
        if (oldPassword != null && user.login(oldPassword)) {
            String newPassword = JOptionPane.showInputDialog("Введите новый пароль:");
            if (newPassword != null) {
                if (!user.isPasswordRestrictionsEnabled() || user.isPasswordValid(newPassword)) {
                    user.setPassword(newPassword);
                    authSystem.saveUsers();
                    JOptionPane.showMessageDialog(null, "Пароль успешно изменен.");
                } else {
                    JOptionPane.showMessageDialog(null, "Пароль не соответствует ограничениям.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Неверный старый пароль.");
        }
    }

    private void viewUsers() {
        StringBuilder usersList = new StringBuilder();
        for (User user : authSystem.getUsers()) {
            usersList.append(user.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, usersList.toString());
    }

    private void addUser() {
        String username = JOptionPane.showInputDialog("Введите имя пользователя:");
        if (username != null) {
            authSystem.addUser(username);
        }
    }

    private void blockUser() {
        String username = JOptionPane.showInputDialog("Введите имя пользователя для блокировки:");
        if (username != null) {
            authSystem.blockUser(username);
        }
    }

    private void toggleRestrictions() {
        boolean enabled = Boolean.parseBoolean(JOptionPane.showInputDialog(
                "Включить ограничения на пароли? (true/false):"));
        authSystem.togglePasswordRestrictions(enabled);
    }

    private void setPasswordRules() {
        List<User> users = authSystem.getUsers();
        String[] userNames = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userNames[i] = users.get(i).getUsername();
        }

        String selectedUser = (String) JOptionPane.showInputDialog(
                null,
                "Выберите пользователя для настройки ограничений:",
                "Настройка ограничений",
                JOptionPane.PLAIN_MESSAGE,
                null,
                userNames,
                userNames[0]
        );

        if (selectedUser != null) {
            User user = authSystem.findUser(selectedUser);
            if (user != null) {
                JPanel rulesPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                JTextField minLengthField = new JTextField(String.valueOf(user.getMinPasswordLength()));
                JTextField maxLengthField = new JTextField(String.valueOf(user.getMaxPasswordLength()));
                JTextField minDigitsField = new JTextField(String.valueOf(user.getMinDigit()));
                JTextField minSpecialCharsField = new JTextField(String.valueOf(user.getMinSpecialChars()));

                rulesPanel.add(new JLabel("Минимальная длина пароля:"));
                rulesPanel.add(minLengthField);
                rulesPanel.add(new JLabel("Максимальная длина пароля:"));
                rulesPanel.add(maxLengthField);
                rulesPanel.add(new JLabel("Минимальное количество цифр:"));
                rulesPanel.add(minDigitsField);
                rulesPanel.add(new JLabel("Минимальное количество спецсимволов:"));
                rulesPanel.add(minSpecialCharsField);

                int result = JOptionPane.showConfirmDialog(null, rulesPanel,
                        "Настройка ограничений", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int minLength = Integer.parseInt(minLengthField.getText());
                        int maxLength = Integer.parseInt(maxLengthField.getText());
                        int minDigits = Integer.parseInt(minDigitsField.getText());
                        int minSpecialChars = Integer.parseInt(minSpecialCharsField.getText());

                        user.setMinPasswordLength(minLength);
                        user.setMaxPasswordLength(maxLength);
                        user.setMinDigit(minDigits);
                        user.setMinSpecialChars(minSpecialChars);

                        authSystem.saveUsers();
                        JOptionPane.showMessageDialog(null,
                                "Ограничения успешно обновлены для пользователя " + user.getUsername() + ".");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Некорректный ввод. Пожалуйста, введите числа.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Пользователь не найден.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CombinedPasswordSystem().setVisible(true));
    }
}

class PasswordStrengthAnalyzer {
    public String analyzePassword(String password, double speed, int attemptsBeforePause, double pauseDuration) {
        int N = calculateAlphabetSize(password);
        BigInteger M = calculateCombinations(N, password.length());

        String strengthInfo = "Анализ пароля: " + password + "\n" +
                "Мощность алфавита: " + N + "\n" +
                "Количество возможных комбинаций: " + M + "\n";

        if (M.compareTo(BigInteger.ZERO) > 0) {
            String timeEstimate = estimateCrackingTime(M, speed, attemptsBeforePause, pauseDuration);
            strengthInfo += "\nОценка времени взлома:\n" + timeEstimate;
        }

        return strengthInfo;
    }

    private int calculateAlphabetSize(String password) {
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[A-Za-z0-9]*");

        int N = 0;
        if (hasLower) N += 26;
        if (hasUpper) N += 26;
        if (hasDigit) N += 10;
        if (hasSpecial) N += 33;

        return N;
    }

    private BigInteger calculateCombinations(int alphabetSize, int passwordLength) {
        try {
            return BigInteger.valueOf(alphabetSize).pow(passwordLength);
        } catch (ArithmeticException e) {
            return BigInteger.ZERO;
        }
    }

    private String estimateCrackingTime(BigInteger combinations, double speed, int m, double v) {
        BigDecimal totalTimeSeconds;
        BigDecimal bigCombinations = new BigDecimal(combinations);
        BigDecimal bigSpeed = BigDecimal.valueOf(speed);

        totalTimeSeconds = bigCombinations.divide(bigSpeed, 50, RoundingMode.HALF_UP);

        if (m > 0) {
            BigDecimal pauses = bigCombinations.divide(BigDecimal.valueOf(m), 0, RoundingMode.DOWN)
                    .multiply(BigDecimal.valueOf(v));
            totalTimeSeconds = totalTimeSeconds.add(pauses);
        }

        BigDecimal secondsInYear = BigDecimal.valueOf(365 * 24 * 60 * 60);
        BigDecimal secondsInMonth = BigDecimal.valueOf(30 * 24 * 60 * 60);
        BigDecimal secondsInDay = BigDecimal.valueOf(24 * 60 * 60);
        BigDecimal secondsInHour = BigDecimal.valueOf(60 * 60);
        BigDecimal secondsInMinute = BigDecimal.valueOf(60);

        BigDecimal[] yearsDiv = totalTimeSeconds.divideAndRemainder(secondsInYear);
        BigDecimal years = yearsDiv[0];
        BigDecimal remainingSeconds = yearsDiv[1];

        BigDecimal[] monthsDiv = remainingSeconds.divideAndRemainder(secondsInMonth);
        BigDecimal months = monthsDiv[0];
        remainingSeconds = monthsDiv[1];

        BigDecimal[] daysDiv = remainingSeconds.divideAndRemainder(secondsInDay);
        BigDecimal days = daysDiv[0];
        remainingSeconds = daysDiv[1];

        BigDecimal[] hoursDiv = remainingSeconds.divideAndRemainder(secondsInHour);
        BigDecimal hours = hoursDiv[0];
        remainingSeconds = hoursDiv[1];

        BigDecimal[] minutesDiv = remainingSeconds.divideAndRemainder(secondsInMinute);
        BigDecimal minutes = minutesDiv[0];
        BigDecimal seconds = minutesDiv[1];

        return String.format(
                "Лет: %s\nМесяцев: %s\nДней: %s\nЧасов: %s\nМинут: %s\nСекунд: %.2f",
                years.setScale(0, RoundingMode.DOWN),
                months.setScale(0, RoundingMode.DOWN),
                days.setScale(0, RoundingMode.DOWN),
                hours.setScale(0, RoundingMode.DOWN),
                minutes.setScale(0, RoundingMode.DOWN),
                seconds.doubleValue()
        );
    }
}

class PasswordCracker {
    private AuthenticationSystem authSystem;
    private Map<Character, Character> russianToLatinMap;
    private List<String> dictionary;

    public PasswordCracker(AuthenticationSystem authSystem) {
        this.authSystem = authSystem;
        initializeRussianToLatinMap();
        loadDictionary();
    }

    private void initializeRussianToLatinMap() {
        russianToLatinMap = new HashMap<>();
        russianToLatinMap.put('а', 'f');
        russianToLatinMap.put('б', ',');
        russianToLatinMap.put('в', 'd');
        russianToLatinMap.put('г', 'u');
        russianToLatinMap.put('д', 'l');
        russianToLatinMap.put('е', 't');
        russianToLatinMap.put('ё', '`');
        russianToLatinMap.put('ж', ';');
        russianToLatinMap.put('з', 'p');
        russianToLatinMap.put('и', 'b');
        russianToLatinMap.put('й', 'q');
        russianToLatinMap.put('к', 'r');
        russianToLatinMap.put('л', 'k');
        russianToLatinMap.put('м', 'v');
        russianToLatinMap.put('н', 'y');
        russianToLatinMap.put('о', 'j');
        russianToLatinMap.put('п', 'g');
        russianToLatinMap.put('р', 'h');
        russianToLatinMap.put('с', 'c');
        russianToLatinMap.put('т', 'n');
        russianToLatinMap.put('у', 'e');
        russianToLatinMap.put('ф', 'a');
        russianToLatinMap.put('х', '[');
        russianToLatinMap.put('ц', 'w');
        russianToLatinMap.put('ч', 'x');
        russianToLatinMap.put('ш', 'i');
        russianToLatinMap.put('щ', 'o');
        russianToLatinMap.put('ъ', ']');
        russianToLatinMap.put('ы', 's');
        russianToLatinMap.put('ь', 'm');
        russianToLatinMap.put('э', '\'');
        russianToLatinMap.put('ю', '.');
        russianToLatinMap.put('я', 'z');
    }

    private void loadDictionary() {
        try {
            dictionary = Files.readAllLines(Paths.get("russian_dictionary.txt"));
        } catch (IOException e) {
            dictionary = new ArrayList<>();
            // Базовый словарь, если файл не найден
            Collections.addAll(dictionary,
                    "пароль", "логин", "админ", "секрет", "доступ",
                    "пользователь", "система", "безопасность", "код", "привет");
        }
    }

    public String dictionaryAttack(String username) {
        User user = authSystem.findUser(username);
        if (user == null) {
            return "Пользователь не найден";
        }

        long startTime = System.currentTimeMillis();
        int attempts = 0;

        for (String word : dictionary) {
            String password = convertRussianToLatin(word);
            attempts++;

            if (user.login(password)) {
                long endTime = System.currentTimeMillis();
                double speed = attempts / ((endTime - startTime) / 1000.0);
                return String.format(
                        "Пароль найден: %s\nПопыток: %d\nВремя: %.2f сек\nСкорость: %.2f попыток/сек",
                        password, attempts, (endTime - startTime) / 1000.0, speed
                );
            }
        }

        long endTime = System.currentTimeMillis();
        double speed = attempts / ((endTime - startTime) / 1000.0);
        return String.format(
                "Пароль не найден\nПопыток: %d\nВремя: %.2f сек\nСкорость: %.2f попыток/сек",
                attempts, (endTime - startTime) / 1000.0, speed
        );
    }

    public String bruteForceAttack(String username, int maxLength) {
        User user = authSystem.findUser(username);
        if (user == null) {
            return "Пользователь не найден";
        }

        long startTime = System.currentTimeMillis();
        int attempts = 0;
        String foundPassword = null;

        for (int length = 1; length <= maxLength; length++) {
            char[] chars = new char[length];
            Arrays.fill(chars, 'a');

            while (true) {
                String password = new String(chars);
                attempts++;

                if (user.login(password)) {
                    foundPassword = password;
                    break;
                }

                if (!incrementChars(chars)) {
                    break;
                }
            }

            if (foundPassword != null) {
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        double speed = attempts / ((endTime - startTime) / 1000.0);

        if (foundPassword != null) {
            return String.format(
                    "Пароль найден: %s\nПопыток: %d\nВремя: %.2f сек\nСкорость: %.2f попыток/сек",
                    foundPassword, attempts, (endTime - startTime) / 1000.0, speed
            );
        } else {
            return String.format(
                    "Пароль не найден (макс. длина %d)\nПопыток: %d\nВремя: %.2f сек\nСкорость: %.2f попыток/сек",
                    maxLength, attempts, (endTime - startTime) / 1000.0, speed
            );
        }
    }

    public String combinedAttack(String username, int maxLength) {
        User user = authSystem.findUser(username);
        if (user == null) {
            return "Пользователь не найден";
        }

        long startTime = System.currentTimeMillis();
        int attempts = 0;
        String foundPassword = null;

        // Сначала пробуем словарные атаки
        for (String word : dictionary) {
            String password = convertRussianToLatin(word);
            attempts++;

            if (user.login(password)) {
                foundPassword = password;
                break;
            }

            // Пробуем варианты с добавлением цифр
            for (int i = 0; i < 10; i++) {
                String numPassword = password + i;
                attempts++;

                if (user.login(numPassword)) {
                    foundPassword = numPassword;
                    break;
                }
            }

            if (foundPassword != null) {
                break;
            }
        }

        // Если словарная атака не помогла, пробуем полный перебор
        if (foundPassword == null) {
            for (int length = 1; length <= maxLength; length++) {
                char[] chars = new char[length];
                Arrays.fill(chars, 'a');

                while (true) {
                    String password = new String(chars);
                    attempts++;

                    if (user.login(password)) {
                        foundPassword = password;
                        break;
                    }

                    if (!incrementChars(chars)) {
                        break;
                    }
                }

                if (foundPassword != null) {
                    break;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        double speed = attempts / ((endTime - startTime) / 1000.0);

        if (foundPassword != null) {
            return String.format(
                    "Пароль найден: %s\nПопыток: %d\nВремя: %.2f сек\nСкорость: %.2f попыток/сек",
                    foundPassword, attempts, (endTime - startTime) / 1000.0, speed
            );
        } else {
            return String.format(
                    "Пароль не найден (макс. длина %d)\nПопыток: %d\nВремя: %.2f сек\nСкорость: %.2f попыток/сек",
                    maxLength, attempts, (endTime - startTime) / 1000.0, speed
            );
        }
    }

    private String convertRussianToLatin(String russianWord) {
        StringBuilder result = new StringBuilder();
        for (char c : russianWord.toCharArray()) {
            Character latinChar = russianToLatinMap.get(c);
            if (latinChar != null) {
                result.append(latinChar);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private boolean incrementChars(char[] chars) {
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] < 'z') {
                chars[i]++;
                return true;
            } else {
                chars[i] = 'a';
            }
        }
        return false;
    }
}