package tis.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("account-queue")
class AccountQueueProperty(
    val queueSize: Int = 3,
)
