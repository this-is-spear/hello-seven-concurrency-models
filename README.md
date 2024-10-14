## 1-1 실험해본 기록

초기 `입금`, `출금`, `이체` 로직은 다음과 같다.

```kotlin
class Deposit(  
    private val user: User,  
    private val money: Money,  
) {  
    fun execute1() {  
        user.balance += money  
    }
}

class Transfer(  
    private val source: User,  
    private val target: User,  
    private val money: Money,  
) {  
    fun execute() {  
        source.balance -= money  
        target.balance += money  
    }
}

class Withdraw(  
    private val user: User,  
    private val money: Money,  
) {  
    fun execute() {  
        user.balance -= money  
    }
}
```

### 비결정성 특징 해결
 동시성은 비결정성 특징을 가져 결과가 달라진다. 다음은 비결정성으로 의도한 결과가 나오지 않는 경우다.  

```kotlin
@RepeatedTest(100)  
fun codeTest1Fail() {  
    val user1 = User("user1")  
    val user2 = User("user2")  
    val threadPool = CustomThreadPool()  
    val iteration = 100  
    for (i in 1..iteration) {  
        threadPool.submit { Deposit(user1, Money(100)).execute1() }  
        threadPool.submit { Deposit(user2, Money(100)).execute1() }  
        threadPool.submit { Withdraw(user2, Money(100)).execute1() }  
        threadPool.submit { Transfer(user1, user2, Money(50)).execute1() }  
    }  
  
    threadPool.runAll()  
    threadPool.joinAll()  
  
    assertEquals(100 * iteration - 50 * iteration, user1.balance.amount)  
    assertEquals(50 * iteration, user1.balance.amount)  
}

```

비결정성 특징을 제거하기 위해 동기화한다. 동기화 방법으로 명령어를 순차적으로 실행한다. 그러나 동기화 컨텍스트가 넓어질 수록 동시성이 떨어진다.

```kotlin
@RepeatedTest(100)  
fun codeTest1Success1() {  
    val user1 = User("user1")  
    val user2 = User("user2")  
    val threadPool = CustomThreadPool()  
    val iteration = 100  
    for (i in 1..iteration) {  
        threadPool.submit { synchronized(this) { Deposit(user1, Money(100)).execute1() } }  
        threadPool.submit { synchronized(this) { Deposit(user2, Money(100)).execute1() } }  
        threadPool.submit { synchronized(this) { Withdraw(user2, Money(100)).execute1() } }  
        threadPool.submit { synchronized(this) { Transfer(user1, user2, Money(50)).execute1() } }  
    }  
  
    threadPool.runAll()  
    threadPool.joinAll()  
  
    assertEquals(100 * iteration - 50 * iteration, user1.balance.amount)  
    assertEquals(50 * iteration, user1.balance.amount)  
}  
  
```

동기화 컨텍스트를 좁혀 리소스 사용량을 높였다. 이 때 사용자 객체를 기준으로 동기화한다.  

```kotlin
class Deposit(  
    private val user: User,  
    private val money: Money,  
) {  
    fun execute1() {  
	    synchronized(user) {  
		    user.balance += money  
		}
    }
}

class Transfer(  
    private val source: User,  
    private val target: User,  
    private val money: Money,  
) {  
    fun execute() {  
	    synchronized(source) {  
		    synchronized(target) {  
		        source.balance -= money  
		        target.balance += money  
		    }  
		}
    }
}

class Withdraw(  
    private val user: User,  
    private val money: Money,  
) {  
    fun execute() {  
	    synchronized(user) {  
		    user.balance -= money  
		}
    }
}
```

### 데드락 발생 가능성 해결
사용자를 기준으로 동기화를 진행하는 경우 데드락 발생 가능성이 높다. 

```kotlin
@Test
fun codeTest3Fail() {  
    val user1 = User("user1")  
    val user2 = User("user2")  
    val user3 = User("user3")  
    val user4 = User("user4")  
    val user5 = User("user1")  
    val threadPool = CustomThreadPool()  
    val listOf = listOf(user1, user2, user3, user4, user5)  
  
    listOf.forEach {  
        Deposit(it, Money(100)).execute1()  
    }  
  
    for (i in 1..100) {  
        threadPool.submit { Transfer(user1, user2, Money(100)).execute() }  
        threadPool.submit { Transfer(user2, user3, Money(100)).execute() }  
        threadPool.submit { Transfer(user3, user4, Money(100)).execute() }  
        threadPool.submit { Transfer(user4, user5, Money(100)).execute() }  
        threadPool.submit { Transfer(user5, user1, Money(100)).execute() }  
    }  
  
    threadPool.runAll()  
    threadPool.joinAll()  
  
    listOf.forEach {  
        assertEquals(100, it.balance.amount)  
    }  
}  
```

데드락은 동기화 순서를 순차적으로 수행하는 경우 해결할 수 있다. 잠금 장치 획득 순서가 원형적인 형태이면 데드락이 발생하고 그렇지 않다면 발생하지 않는다.  

![image](https://github.com/user-attachments/assets/8ca9715d-e5ea-41b9-a2f9-a35f82fb7c0e)

사용자를 정렬할 수 있다면 락 획득에 순서를 정의해 문제를 해결할 수 있다.

```kotlin
  
class Transfer(  
    private val source: User,  
    private val target: User,  
    private val money: Money,  
) {  
    fun execute() {  
        return if (source < target) {  
            synchronized(source) {  
                synchronized(target) {  
                    source.balance -= money  
                    target.balance += money  
                }  
            }        
		} else {  
            synchronized(target) {  
                synchronized(source) {  
                    source.balance -= money  
                    target.balance += money  
                }  
            }        
		}  
    }  
}
```

### 그 외 위험들
동시성에서 비결정성을 해결하고 싶을 때 남은 문제는 다음과 같다.

- 가시성 문제 : 읽기 스레드와 쓰기 스레드간 동기화 필요
- 명령어 순서 변경 위험 : 명령어 순서 변경에 따른 의도하지 않은 결과 발생
- 외부 메서드 호출 위험 : 동기화시 외부 메서드 호출로 인해 새로운 잠금 장치 획득 시도하는 위험도 발생

이곳에서 현실적인 문제는  외부 메서드 호출 위험이다. 동시성을 가질 때 비결정성을 해결하기 위해서 동기화를 진행하게 되는데, 동기화를 진행하면서 데이터베이스 처럼 상태를 외부에서 관리하는 등의 동작으로 외부 메서드를 호출할 가능성은 굉장히 높다.

책에서 일반적인 잠금 장치를 이용할 때 외부 메서드 호출을 자제해야 한다고 하는데, 요즘 서비스는 외부 메서드 호출하는 로직이 많다. 어떻게 하면 해결할 수 있을지 고민해야 한다.

