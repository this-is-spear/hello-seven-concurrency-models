package tis.service

import java.util.PriorityQueue
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior

private const val BUFFER_SIZE = 5

@Service
class AccountService {
    private val buffer = PriorityQueue<AccountingBehavior>()
    private val lock = ReentrantLock()
    private val notFull: Condition = lock.newCondition()
    private val notEmpty: Condition = lock.newCondition()

    fun produce(item: AccountingBehavior) {
        lock.lock()
        try {
            while (buffer.size == BUFFER_SIZE) {
                println("Buffer is full, waiting... [$item]")
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
                println("Buffer is empty, waiting...")
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
