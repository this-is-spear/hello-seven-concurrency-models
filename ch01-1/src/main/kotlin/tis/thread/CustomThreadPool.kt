package tis.thread

class CustomThreadPool(
    private val tasks: MutableList<Thread> = mutableListOf()
) {
    fun submit(runnable: Runnable) {
        synchronized(tasks) {
            tasks.add(Thread(runnable))
        }
    }

    fun runAll() {
        val newTasks: List<Thread> = synchronized(this) {
            tasks.toList()
        }
        newTasks.forEach {
            it.start()
        }
    }

    fun joinAll() {
        val newTasks: List<Thread> = synchronized(this) {
            tasks.toList()
        }
        newTasks.forEach {
            it.join()
        }
    }
}
