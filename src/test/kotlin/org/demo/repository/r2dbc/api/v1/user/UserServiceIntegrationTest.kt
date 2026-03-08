package org.demo.repository.r2dbc.api.v1.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import org.demo.repository.r2dbc.api.v1.user.dto.CreateUserDto
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataAccessException

@SpringBootTest
class UserServiceIntegrationTest(
    private val userService: UserService,
) : BehaviorSpec({

        Given("중복 회원가입") {
            When("유효한 입력값이 주어지면") {
                val validCreateUserDto =
                    CreateUserDto(
                        nickname = "this_is_test",
                        username = "test999922",
                        password = "test1234@",
                    )

                Then("DataAccessException이 발생한다") {
                    val exception =
                        shouldThrow<DataAccessException> {
                            userService.signUp(validCreateUserDto)
                        }
                }
            }
        }
    })
