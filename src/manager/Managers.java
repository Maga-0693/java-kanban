package manager;

import java.io.File;

public class Managers {
    //возвращаю реализацию менеджера задач InMemoryTaskManager по умолчанию
    public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    //метод для возвращения истории просмотров
    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManager(File file) {

        return new FileBackedTaskManager(file);
    }
}