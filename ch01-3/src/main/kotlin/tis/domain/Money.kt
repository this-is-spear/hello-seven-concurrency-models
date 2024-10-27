package tis.domain

data class Money(
    val amount: Int = 0,
) {
    init {
        require(amount >= 0) { "The amount must be greater than or equal to 0." }
    }

    operator fun minus(money: Money): Money {
        return Money(amount - money.amount)
    }

    operator fun plus(money: Money): Money {
        return Money(amount + money.amount)
    }
}
