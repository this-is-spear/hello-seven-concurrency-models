package tis.domain

import java.time.Clock
import java.time.Instant
import java.util.UUID

sealed class AccountingBehavior(
    val member: Member,
    val money: Money,
    private val createdAt: Instant = Clock.systemDefaultZone().instant(),
    val transactionSequence: TransactionSequence = TransactionSequence(createdAt, UUID.randomUUID()),
) : Comparable<AccountingBehavior> {
    class Deposit(member: Member, money: Money) : AccountingBehavior(member, money)
    class Withdraw(member: Member, money: Money) : AccountingBehavior(member, money)

    override fun compareTo(other: AccountingBehavior): Int {
        if (this.createdAt == other.createdAt) {
            return (member.id - other.member.id).toInt()
        }
        return this.createdAt.compareTo(other.createdAt)
    }

    override fun toString(): String {
        return "AccountingBehavior(user=$member, money=$money, createdAt=${createdAt})"
    }
}
