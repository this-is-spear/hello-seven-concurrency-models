package tis

class Deposit(
    private val user: User,
    private val money: Money,
) {
    fun execute1() {
        user.balance += money
    }

    fun execute2() {
        synchronized(user) {
            user.balance += money
        }
    }
}
