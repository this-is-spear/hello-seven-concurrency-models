package tis.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tis.service.DepositService
import tis.service.TransferService
import tis.service.WithdrawService

@RestController
class AccountingController(
    private val depositService: DepositService,
    private val withdrawService: WithdrawService,
    private val transferService: TransferService,
) {
    @PostMapping("/deposit")
    fun depositMoney(
        @RequestParam userId: Long,
        @RequestParam amount: Int,
    ) {
        depositService.execute(userId, amount)
    }

    @PostMapping("/withdraw")
    fun withdrawMoney(
        @RequestParam userId: Long,
        @RequestParam amount: Int,
    ) {
        withdrawService.execute(userId, amount)
    }

    @PostMapping("/transfer")
    fun transferMoney(
        @RequestParam fromUserId: Long,
        @RequestParam toUserId: Long,
        @RequestParam amount: Int,
    ) {
        transferService.execute(fromUserId, toUserId, amount)
    }
}
