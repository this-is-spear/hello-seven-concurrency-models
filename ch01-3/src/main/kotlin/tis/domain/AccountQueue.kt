package tis.domain

import java.util.PriorityQueue
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

private const val BUFFER_SIZE = 5

class AccountQueue(
    private val queueId: Long,
) {
    private val buffer = PriorityQueue<AccountingBehavior>()
    private val lock = ReentrantLock()
    private val notFull: Condition = lock.newCondition()
    private val notEmpty: Condition = lock.newCondition()

    fun produce(item: AccountingBehavior) {
        lock.lock()
        try {
            while (buffer.size == BUFFER_SIZE) {
                println("Buffer$queueId is full, waiting... [$item]")
                notFull.await()
            }

            buffer.offer(item)
            println("Produced: $item")
            notEmpty.signal()
        } finally {
            lock.unlock()
        }
    }

    fun consume(): AccountingBehavior {
        lock.lock()
        try {
            while (buffer.isEmpty()) {
                println("Buffer$queueId is empty, waiting...")
                notEmpty.await()
            }

            val item = buffer.poll()
            println("Consumed: $item")
            notFull.signal()
            return item
        } finally {
            lock.unlock()
        }
    }
}