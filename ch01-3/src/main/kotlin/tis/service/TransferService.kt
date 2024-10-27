package tis.service

import org.springframework.stereotype.Service

@Service
class TransferService(
    private val depositService: DepositService,
    private val withdrawService: WithdrawService,
) {
    fun execute(fromUserId: Long, toUserId: Long, amount: Int) {
        depositService.execute(toUserId, amount)
        withdrawService.execute(fromUserId, amount)
    }
}
