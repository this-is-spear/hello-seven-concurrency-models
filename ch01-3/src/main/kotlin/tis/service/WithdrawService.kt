package tis.service

import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior
import tis.domain.Money
import tis.domain.Member

@Service
class WithdrawService(
    private val accountFacadeService: ProduceService,
) {
    fun execute(userId: Long, amount: Int) {
        val member = Member(id = userId)
        val money = Money(amount)
        accountFacadeService.produce(AccountingBehavior.Withdraw(member, money))
    }
}
