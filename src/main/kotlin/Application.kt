import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.NotFoundException
import io.ktor.features.ParameterConversionException
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.cio.EngineMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import models.Post
import repositories.PostRepository
import repositories.PostRepositoryInMemory
import kotlin.coroutines.EmptyCoroutineContext

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    val repository: PostRepository = PostRepositoryInMemory()

    with(CoroutineScope(EmptyCoroutineContext)) {
        launch {

            repository.save(
                Post(
                    0,
                    "Is Video and Event Post",
                    "Danill Sterlikov",
                    170484646,
                    15,
                    82,
                    3,
                    33.1546,
                    44.46847,
                    "https://www.youtube.com/watch?v=WhWc3b3KhnY"
                )
            )


            repository.save(
                Post(
                    0,
                    "Secondary post with very-very long title. Really very long title.",
                    "Ivan Ivanov",
                    170400000,
                    7,
                    81,
                    15
                )
            )

            repository.save(
                Post(
                    0,
                    "Is Event Post",
                    "Kolya",
                    170400999,
                    71,
                    810,
                    1,
                    33.1546,
                    44.46847
                )
            )

            val sourcePost = Post(
                0,
                "Is only video Post",
                "Kolya",
                170400999,
                71,
                810,
                1,
                null,
                null,
                "https://www.youtube.com/watch?v=WhWc3b3KhnY"
            )

            repository.save(sourcePost)

            repository.save(
                Post(
                    0,
                    "Is sharing of previous",
                    "Marya Petrosyan",
                    170401000,
                    3,
                    1550,
                    0,
                    null,
                    null,
                    null,
                    sourcePost
                )
            )

            repository.save(
                Post(
                    0,
                    "Is Advertising",
                    "Google",
                    180400999,
                    0,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null,
                    "https://google.com"
                )
            )

        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(StatusPages) {

    }

    install(Routing) {

        route("api/v1/posts") {

            get {
                call.respond(repository.getAll())
            }

            get("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                val model = repository.getById(id) ?: throw NotFoundException()

                call.respond(model)

            }

            delete("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                repository.removeById(id)
                call.respond(HttpStatusCode.NoContent)


            }

            post {

                val model = call.receive<Post>()
                repository.save(model)
                call.respond(model)

            }

        }

        route("/api/v1/like") {

            get("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                val model = repository.getById(id) ?: throw NotFoundException()

                repository.likeById(id)
                call.respond(model)

            }

        }

        route("/api/v1/dislike") {

            get("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                val model = repository.getById(id) ?: throw NotFoundException()

                repository.dislikeById(id)
                call.respond(model)

            }

        }

    }

}