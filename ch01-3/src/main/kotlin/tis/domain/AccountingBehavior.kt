package tis.domain

import java.time.Clock

sealed class AccountingBehavior(
    val member: Member,
    val money: Money,
    private val createdAt: Clock = Clock.systemDefaultZone(),
) : Comparable<AccountingBehavior> {
    class Deposit(member: Member, money: Money) : AccountingBehavior(member, money)
    class Withdraw(member: Member, money: Money) : AccountingBehavior(member, money)
    class Transfer(
        member: Member,
        money: Money,
        val targetMember: Member
    ) : AccountingBehavior(member, money)

    override fun compareTo(other: AccountingBehavior): Int {
        if (this.createdAt.instant() == other.createdAt.instant()) {
            return (member.id - other.member.id).toInt()
        }
        return this.createdAt.instant().compareTo(other.createdAt.instant())
    }

    override fun toString(): String {
        return "AccountingBehavior(user=$member, money=$money, createdAt=${createdAt.instant()})"
    }
}
