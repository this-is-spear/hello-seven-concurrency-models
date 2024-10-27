package tis.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tis.domain.AccountingBehavior
import tis.repository.MemberRepository

@Service
class RecordService(
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun withdraw(behavior: AccountingBehavior.Withdraw) {
        val member = memberRepository.findByIdOrNull(behavior.member.id)
            ?: throw IllegalArgumentException("No user data")
        member.account -= behavior.money
        memberRepository.save(member)
    }

    @Transactional
    fun deposit(behavior: AccountingBehavior.Deposit) {
        val member = memberRepository.findByIdOrNull(behavior.member.id)
            ?: throw IllegalArgumentException("No user data")
        member.account += behavior.money
        memberRepository.save(member)
    }

    @Transactional
    fun transfer(behavior: AccountingBehavior.Transfer) {
        val member = memberRepository.findByIdOrNull(behavior.member.id)
            ?: throw IllegalArgumentException("No user data")
        val targetMember = memberRepository.findByIdOrNull(behavior.member.id)
            ?: throw IllegalArgumentException("No user data")
        member.account -= behavior.money
        targetMember.account += behavior.money
        memberRepository.save(member)
        memberRepository.save(targetMember)
    }
}
