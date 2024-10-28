package tis.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tis.domain.AccountingBehavior
import tis.repository.MemberRepository

@Service
class AccountService(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun withdraw(behavior: AccountingBehavior.Withdraw) {
        val member = behavior.member
        member.account -= behavior.money
        memberRepository.save(member)
    }

    @Transactional
    fun deposit(behavior: AccountingBehavior.Deposit) {
        val member = behavior.member
        member.account += behavior.money
        memberRepository.save(member)
    }
}
