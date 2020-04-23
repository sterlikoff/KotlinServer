import classes.StubData
import exceptions.AccessDeniedException
import exceptions.PasswordChangeException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.jwt
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
import kotlinx.coroutines.runBlocking
import models.*
import services.JWTTokenService
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import repositories.PostRepository
import repositories.PostRepositoryInMemory
import repositories.UserRepository
import repositories.UserRepositoryInMemory
import services.PostService
import services.UserService

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    install(KodeinFeature) {

        bind<PostRepository>() with eagerSingleton {
            PostRepositoryInMemory()
        }

        bind<UserRepository>() with eagerSingleton {
            UserRepositoryInMemory()
        }

        bind<PasswordEncoder>() with eagerSingleton {
            BCryptPasswordEncoder()
        }

        bind<JWTTokenService>() with eagerSingleton {
            JWTTokenService()
        }

        bind<PostService>() with eagerSingleton {
            PostService(instance())
        }

        bind<UserService>() with eagerSingleton {
            UserService(instance(), instance(), instance())
        }

    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(StatusPages) {

        exception<NotFoundException> {
            call.respond(HttpStatusCode.NotFound)
        }

        exception<ParameterConversionException> {
            call.respond(HttpStatusCode.BadRequest)
        }

        exception<PasswordChangeException> {
            call.respond(HttpStatusCode.Forbidden)
        }

        exception<AccessDeniedException> {
            call.respond(HttpStatusCode.Forbidden)
        }

    }

    install(Authentication) {

        val jwtService by kodein().instance<JWTTokenService>()
        val userService by kodein().instance<UserService>()

        jwt {

            verifier(jwtService.verifier)
            validate {
                val id = it.payload.getClaim("id").asInt()
                userService.getModelById(id)
            }

        }

    }

    install(Routing) {
        v1()
    }

    runBlocking {
        StubData.init(this@module)
    }

}

fun Routing.v1() {

    val postService by kodein().instance<PostService>()
    val userService by kodein().instance<UserService>()

    authenticate {

        route("api/v1/profile") {

            get {

                val me = call.authentication.principal<User>()
                call.respond(me!!)

            }

        }

        route("api/v1/posts") {

            get {
                val all = postService.getAll()
                println(all.size)
                call.respond(all)
            }

            get("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                val model = postService.getById(id)

                call.respond(model)

            }

            delete("/{id}") {

                val user = call.authentication.principal<User>() ?: throw NotFoundException()

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                postService.remove(id, user)
                call.respond(HttpStatusCode.NoContent)

            }

            post("/create") {

                val user = call.authentication.principal<User>() ?: throw NotFoundException()
                call.respond(postService.save(0, call.receive(), user))

            }

            post("/update/{id}") {

                val user = call.authentication.principal<User>() ?: throw NotFoundException()

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                call.respond(postService.save(id, call.receive(), user))

            }

            get("/like/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                call.respond(postService.like(id))

            }

            get("/dislike/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                call.respond(postService.dislike(id))

            }

            get("/share/{id}") {

                val id = call.parameters["id"]?.toIntOrNull() ?: throw ParameterConversionException("id", "Int")
                call.respond( postService.share(id))

            }

        }

    }

    route("api/v1/registration") {

        post {

            val model: RegistrationRequestDto = call.receive()
            userService.registration(model)
            call.respond(HttpStatusCode.NoContent)

        }

    }

    route("api/v1/authentication") {

        post {

            val input = call.receive<AuthenticationRequestDto>()
            val response = userService.authenticate(input)
            call.respond(response)

        }

    }

}