package ru.job4j.cache;

import java.util.Scanner;

public class Emulator {
    public static final Integer CHOOSE_DIR = 1;
    public static final Integer LOAD_DATA = 2;
    public static final Integer GET_DATA = 3;

    public static final String SELECT = "Выберите меню";
    public static final String DIR = "Укажите директорию";
    public static final String LOAD = "Укажите файл для загрузки";
    public static final String GET = "Укажите файл для получения информации";
    public static final String EXIT = "Конец работы";

    public static final String MENU = "Введите 1 для создание поста.\n"
            + "Введите 1, чтобы указать директорию.\n"
            + "Введите 2, загрузить содержимое файла.\n"
            + "Введите 3, получить содержимое файла.\n"
            + "Введите любое другое число для выхода.;\n";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DirFileCache cache = new DirFileCache("./");
        boolean run = true;
        while (run) {
            System.out.println(MENU);
            System.out.println(SELECT);
            int userChoice = Integer.parseInt(scanner.nextLine());
            if (CHOOSE_DIR == userChoice) {
                System.out.println(DIR);
                String dir = scanner.nextLine();
                cache = new DirFileCache(dir);
            } else if (LOAD_DATA == userChoice) {
                System.out.println(LOAD);
                String file = scanner.nextLine();
                cache.get(file);
            } else if (GET_DATA == userChoice) {
                System.out.println(GET);
                String file = scanner.nextLine();
                System.out.println(cache.get(file));
            } else {
                run = false;
                System.out.println(EXIT);
            }
        }
    }
}
