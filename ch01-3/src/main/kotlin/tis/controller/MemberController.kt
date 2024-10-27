package tis.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tis.domain.Member
import tis.service.MemberService

@RestController
@RequestMapping("/members")
class MemberController(
    private val memberService: MemberService,
) {
    @PostMapping
    fun join(@RequestParam name: String): Long {
        return memberService.join(name)
    }

    @GetMapping
    fun myInformation(@RequestParam userId: Long): Member {
        return memberService.me(userId)
    }
}
