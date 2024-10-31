package tis.service

import org.springframework.stereotype.Service
import tis.domain.AccountingBehavior
import tis.domain.Member
import tis.domain.Money

@Service
class WithdrawService(
    private val accountFacadeService: ProducerConsumerService,
    private val transactionService: TransactionService,
) {
    fun execute(userId: Long, amount: Int) {
        val member = Member(id = userId)
        val money = Money(amount)
        val item = AccountingBehavior.Withdraw(member, money)
        transactionService.start(item.transactionSequence.sequence)
        accountFacadeService.produce(item)
        if (!transactionService.isCompleted(item.transactionSequence.sequence)) {
            throw IllegalStateException("Transaction failed")
        }
    }
}
