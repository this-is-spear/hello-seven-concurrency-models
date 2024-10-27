package tis.repository

import org.springframework.data.jpa.repository.JpaRepository
import tis.domain.Member

interface MemberRepository : JpaRepository<Member, Long>
