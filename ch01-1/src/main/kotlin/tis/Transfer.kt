package tis

class Transfer(
    private val source: User,
    private val target: User,
    private val money: Money,
) {
    fun execute1() {
        source.balance -= money
        target.balance += money
    }

    fun execute2() {
        synchronized(source) {
            synchronized(target) {
                source.balance -= money
                target.balance += money
            }
        }
    }

    fun execute3() {
        return if (source < target) {
            synchronized(source) {
                synchronized(target) {
                    source.balance -= money
                    target.balance += money
                }
            }
        } else {
            synchronized(target) {
                synchronized(source) {
                    source.balance -= money
                    target.balance += money
                }
            }
        }
    }
}
