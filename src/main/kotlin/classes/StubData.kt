package classes

import io.ktor.application.Application
import models.PostInputDto
import models.RegistrationRequestDto
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import services.PostService
import services.UserService

class StubData {

    companion object {

        suspend fun init(application: Application) {

            val userService by application.kodein().instance<UserService>()

            userService.registration(
                RegistrationRequestDto(
                    "admin",
                    "admin"
                )
            )

            userService.registration(
                RegistrationRequestDto(
                    "user",
                    "user"
                )
            )

            val user = userService.getModelByUsername("user")

            val postService by application.kodein().instance<PostService>()

            postService.save(
                0,
                PostInputDto(
                    "Is Video and Event Post",
                    "Any text may be in this field - is post content.",
                    33.1546,
                    44.46847,
                    "https://www.youtube.com/watch?v=WhWc3b3KhnY"
                ),
                user
            )

            postService.save(
                0,
                PostInputDto("Secondary post with very-very long title. Really very long title.", ""),
                user
            )

            postService.save(
                0,
                PostInputDto(
                    "Is Event Post",
                    "Any text may be in this field - is post content. Text may be more.",
                    33.1546,
                    44.46847
                ),
                user
            )

            val sourcePost = postService.save(
                0,
                PostInputDto(
                    "Is only video Post",
                    "Any text may be in this field - is post content. \n Many lines \n Third line.",
                    null,
                    null,
                    "https://www.youtube.com/watch?v=WhWc3b3KhnY"
                ),
                user
            )

            postService.share(sourcePost.id)

            postService.save(
                0,
                PostInputDto(
                    "Is Advertising",
                    "Is advertising by Google!!!",
                    null,
                    null,
                    null,
                    "https://google.com"
                ),
                user
            )

        }

    }

}