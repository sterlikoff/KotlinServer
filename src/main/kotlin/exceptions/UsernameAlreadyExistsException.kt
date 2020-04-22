package exceptions

class UsernameAlreadyExistsException(override val message: String): Exception(message)