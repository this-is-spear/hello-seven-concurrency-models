package tis

data class Money(
    val amount: Int = 0,
) {
    operator fun minus(money: Money): Money {
        return Money(amount - money.amount)
    }

    operator fun plus(money: Money): Money {
        return Money(amount + money.amount)
    }
}
