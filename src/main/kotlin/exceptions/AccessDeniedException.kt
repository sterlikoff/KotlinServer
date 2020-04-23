package exceptions

class AccessDeniedException(override val message: String): Exception(message)