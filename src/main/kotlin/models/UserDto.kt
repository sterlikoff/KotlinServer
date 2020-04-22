package models

data class UserDto(
    val id: Int = 0,
    val username: String,
    val password: String
) {

    companion object {

        fun fromModel(model: User) = UserDto(
            model.id,
            model.username,
            model.password
        )

    }

}