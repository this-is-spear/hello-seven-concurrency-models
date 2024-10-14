package tis

import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import tis.thread.CustomThreadPool
import kotlin.test.assertEquals

class MainKtTest {
    /**
     * 동시성은 비결정성 특징을 가지고 결과가 달라진다.
     * 다음은 비결정성으로 의도한 결과가 나오지 않는 경우다.
     */
    @RepeatedTest(100)
    fun codeTest1Fail() {
        val user1 = User("user1")
        val user2 = User("user2")
        val threadPool = CustomThreadPool()
        val iteration = 100
        for (i in 1..iteration) {
            threadPool.submit { Deposit(user1, Money(100)).execute1() }
            threadPool.submit { Deposit(user2, Money(100)).execute1() }
            threadPool.submit { Withdraw(user2, Money(100)).execute1() }
            threadPool.submit { Transfer(user1, user2, Money(50)).execute1() }
        }

        threadPool.runAll()
        threadPool.joinAll()

        assertEquals(100 * iteration - 50 * iteration, user1.balance.amount)
        assertEquals(50 * iteration, user1.balance.amount)
    }

    /**
     * 비결정성 특징을 제거하기 위해 동기화한다.
     * 현재 컨텍스트를 기준으로 명령어를 순차적으로 실행한다.
     *
     * 그러나 동기화 컨텍스트가 넓어질 수록 동시성이 떨어진다.
     */
    @RepeatedTest(100)
    fun codeTest1Success1() {
        val user1 = User("user1")
        val user2 = User("user2")
        val threadPool = CustomThreadPool()
        val iteration = 100
        for (i in 1..iteration) {
            threadPool.submit { synchronized(this) { Deposit(user1, Money(100)).execute1() } }
            threadPool.submit { synchronized(this) { Deposit(user2, Money(100)).execute1() } }
            threadPool.submit { synchronized(this) { Withdraw(user2, Money(100)).execute1() } }
            threadPool.submit { synchronized(this) { Transfer(user1, user2, Money(50)).execute1() } }
        }

        threadPool.runAll()
        threadPool.joinAll()

        assertEquals(100 * iteration - 50 * iteration, user1.balance.amount)
        assertEquals(50 * iteration, user1.balance.amount)
    }

    /**
     * 동기화 컨텍스트를 좁히기 위해 사용자 객체를 기준으로 동기화한다.
     */
    @RepeatedTest(100)
    fun codeTest1Success2() {
        val user1 = User("user1")
        val user2 = User("user2")
        val threadPool = CustomThreadPool()
        val iteration = 100
        for (i in 1..iteration) {
            threadPool.submit { Deposit(user1, Money(100)).execute2() }
            threadPool.submit { Deposit(user2, Money(100)).execute2() }
            threadPool.submit { Withdraw(user2, Money(100)).execute2() }
            threadPool.submit { Transfer(user1, user2, Money(50)).execute2() }
        }

        threadPool.runAll()
        threadPool.joinAll()

        assertEquals(100 * iteration - 50 * iteration, user1.balance.amount)
        assertEquals(50 * iteration, user1.balance.amount)
    }

    /**
     * 사용자를 기준으로 동기화를 진행하는 경우 데드락 발생 가능성이 높다.
     */
    @Test
    fun codeTest3Fail() {
        val user1 = User("user1")
        val user2 = User("user2")
        val user3 = User("user3")
        val user4 = User("user4")
        val user5 = User("user1")
        val threadPool = CustomThreadPool()
        val listOf = listOf(user1, user2, user3, user4, user5)

        listOf.forEach {
            Deposit(it, Money(100)).execute1()
        }

        for (i in 1..100) {
            threadPool.submit { Transfer(user1, user2, Money(100)).execute2() }
            threadPool.submit { Transfer(user2, user3, Money(100)).execute2() }
            threadPool.submit { Transfer(user3, user4, Money(100)).execute2() }
            threadPool.submit { Transfer(user4, user5, Money(100)).execute2() }
            threadPool.submit { Transfer(user5, user1, Money(100)).execute2() }
        }

        threadPool.runAll()
        threadPool.joinAll()

        listOf.forEach {
            assertEquals(100, it.balance.amount)
        }
    }

    /**
     * 데드락은 동기화 순서를 순차적으로 수행하는 경우 해결할 수 있다.
     * 잠금 장치 획득 순서가 원형적인 형태이면 데드락이 발생하고 그렇지 않다면 발생하지 않는다.
     */
    @RepeatedTest(100)
    fun codeTest3Success() {
        val user1 = User("user1")
        val user2 = User("user2")
        val user3 = User("user3")
        val user4 = User("user4")
        val user5 = User("user1")
        val threadPool = CustomThreadPool()
        val listOf = listOf(user1, user2, user3, user4, user5)

        listOf.forEach {
            Deposit(it, Money(100)).execute1()
        }

        for (i in 1..100) {
            threadPool.submit { Transfer(user1, user2, Money(100)).execute3() }
            threadPool.submit { Transfer(user2, user3, Money(100)).execute3() }
            threadPool.submit { Transfer(user3, user4, Money(100)).execute3() }
            threadPool.submit { Transfer(user4, user5, Money(100)).execute3() }
            threadPool.submit { Transfer(user5, user1, Money(100)).execute3() }
        }

        threadPool.runAll()
        threadPool.joinAll()

        listOf.forEach {
            assertEquals(100, it.balance.amount)
        }
    }
}
