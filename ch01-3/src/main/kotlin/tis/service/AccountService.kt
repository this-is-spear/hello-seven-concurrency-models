package tis.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tis.domain.AccountingBehavior

@Service
class AccountService(
    private val memberService: MemberService,
) {
    @Transactional
    fun withdraw(behavior: AccountingBehavior.Withdraw) {
        val me = memberService.me(behavior.member.id)
        me.account -= behavior.money
        memberService.save(me)
    }

    @Transactional
    fun deposit(behavior: AccountingBehavior.Deposit) {
        val me = memberService.me(behavior.member.id)
        me.account += behavior.money
        memberService.save(me)
    }
}
