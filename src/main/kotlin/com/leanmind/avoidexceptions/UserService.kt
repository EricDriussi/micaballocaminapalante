package com.leanmind.avoidexceptions

import org.springframework.stereotype.Service
import java.util.logging.Level
import java.util.logging.Logger

private const val MAX_NUMBER_OF_ADMINS = 2

@Service
class UserService(private val userRepository: UserRepository) {
    val logger: Logger = Logger.getLogger(UserService::class.java.name)

    fun create(user: User): CreateUserResult {
        try {
            if (userRepository.exists(user)) {
                return CreateUserResult.userAlreadyExistsError()
            }
            if (user.isAdmin() && cannotExistsMoreAdmins()) {
                return CreateUserResult.tooManyAdminsError()
            }
            val repoResult = userRepository.save(user)
            if (repoResult.isError()) {
                logger.log(Level.SEVERE, "Cannot create user.")
            } else {
                logger.log(Level.INFO, "User created.")
            }
            return repoResult
        } catch (exception: Exception) {
            logger.log(Level.SEVERE, "Cannot create user.", exception)
            throw CannotCreateUserException(exception)
        }
    }

    private fun cannotExistsMoreAdmins() = userRepository.countOfAdmins() >= MAX_NUMBER_OF_ADMINS
}
