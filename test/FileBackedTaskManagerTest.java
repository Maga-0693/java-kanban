package test;

import manager.FileBackedTaskManager;
import test.TaskManagerTest;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager getTaskManager() {

        return new FileBackedTaskManager(new File("tasks.csv"));
    }
}
