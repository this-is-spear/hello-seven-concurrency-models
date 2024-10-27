package tis.service

import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior
import tis.domain.Member
import tis.domain.Money

@Service
class TransferService(
    private val accountService: AccountService,
) {
    fun execute(fromUserId: Long, toUserId: Long, amount: Int) {
        val fromMember = Member(id = fromUserId)
        val toMember = Member(id = toUserId)
        val money = Money(amount)
        accountService.produce(AccountingBehavior.Transfer(fromMember, money, toMember))
    }
}
