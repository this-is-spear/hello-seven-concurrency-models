package tis.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tis.domain.AccountingBehavior
import tis.repository.MemberRepository

@Service
class AccountService(
    private val memberRepository: MemberRepository,
    private val transactionService: TransactionService,
) {
    @Transactional
    fun withdraw(behavior: AccountingBehavior.Withdraw) {
        try {
            val member = memberRepository.findByIdOrNull(behavior.member.id)
                ?: throw IllegalArgumentException("No user data")
            member.account -= behavior.money
            memberRepository.save(member)
            transactionService.complete(behavior.transactionSequence.sequence)
        } catch (e: Exception) {
            transactionService.fail(behavior.transactionSequence.sequence)
        }
    }

    @Transactional
    fun deposit(behavior: AccountingBehavior.Deposit) {
        try {
            val member = memberRepository.findByIdOrNull(behavior.member.id)
                ?: throw IllegalArgumentException("No user data")
            member.account += behavior.money
            memberRepository.save(member)
            transactionService.complete(behavior.transactionSequence.sequence)
        } catch (e: Exception) {
            transactionService.fail(behavior.transactionSequence.sequence)
        }
    }
}
