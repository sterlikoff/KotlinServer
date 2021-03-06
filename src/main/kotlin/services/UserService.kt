package services

import exceptions.PasswordChangeException
import exceptions.UsernameAlreadyExistsException
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.features.NotFoundException
import models.*
import org.springframework.security.crypto.password.PasswordEncoder
import repositories.UserRepository

class UserService(
    private val repository: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {

    suspend fun getModelById(id: Int): User? {
        return repository.getById(id)
    }

    suspend fun getById(id: Int): UserDto {
        val model = repository.getById(id) ?: throw NotFoundException()
        return UserDto.fromModel(model)
    }

    suspend fun getModelByUsername(username: String): User = repository.getByUsername(username) ?: throw NotFoundException()
    suspend fun getByUsername(username: String): UserDto = UserDto.fromModel(getModelByUsername(username))

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {

        val model = repository.getByUsername(input.username) ?: throw NotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) throw PasswordChangeException("Wrong password!")
        val token = tokenService.generate(model.id)

        return AuthenticationResponseDto(token)
    }

    suspend fun registration(input: RegistrationRequestDto) {

        if (repository.getByUsername(input.username) != null) throw UsernameAlreadyExistsException("Пользователь с таким логином уже зарегистрирован")

        repository.save(
            User(
                0,
                input.username,
                passwordEncoder.encode(input.password)
            )
        )

    }

}
