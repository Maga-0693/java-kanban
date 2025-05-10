package test;

import manager.InMemoryTaskManager;
import test.TaskManagerTest;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}
