package tis.service

import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior
import tis.domain.Member
import tis.domain.Money

@Service
class DepositService(
    private val accountFacadeService: ProducerConsumerService,
    private val memberService: MemberService,
    private val transactionService: TransactionService,
) {
    fun execute(userId: Long, amount: Int) {
        val member = memberService.me(userId)
        val money = Money(amount)
        val item = AccountingBehavior.Deposit(member, money)
        accountFacadeService.produce(item)
        transactionService.start(item.transactionSequence.sequence)

        if (!transactionService.isCompleted(item.transactionSequence.sequence)) {
            throw IllegalStateException("Transaction failed")
        }
    }
}
