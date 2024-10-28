package tis.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import tis.domain.Member
import tis.repository.MemberRepository

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {

    fun join(name: String): Long {
        val member = memberRepository.save(Member(name = name))
        return member.id
    }

    fun me(userId: Long): Member {
        return memberRepository.findByIdOrNull(userId)
            ?: throw IllegalArgumentException("No user data")
    }
}
