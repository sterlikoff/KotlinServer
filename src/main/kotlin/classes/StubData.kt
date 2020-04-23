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

            val postService by application.kodein().instance<PostService>()

            postService.save(
                0,
                PostInputDto(
                    "Is Video and Event Post",
                    "Danill Sterlikov",
                    33.1546,
                    44.46847,
                    "https://www.youtube.com/watch?v=WhWc3b3KhnY"
                )
            )

            postService.save(
                0,
                PostInputDto(
                    "Secondary post with very-very long title. Really very long title.",
                    "Ivan Ivanov"
                )
            )

            postService.save(
                0,
                PostInputDto(
                    "Is Event Post",
                    "Kolya",
                    33.1546,
                    44.46847
                )
            )

            val sourcePost = postService.save(
                0,
                PostInputDto(
                    "Is only video Post",
                    "Kolya",
                    null,
                    null,
                    "https://www.youtube.com/watch?v=WhWc3b3KhnY"
                )
            )

            postService.share(sourcePost.id)

            postService.save(
                0,
                PostInputDto(
                    "Is Advertising",
                    "Google",
                    null,
                    null,
                    null,
                    "https://google.com"
                )
            )

        }

    }

}