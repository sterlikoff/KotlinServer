import com.jayway.jsonpath.JsonPath
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import org.junit.Test

class ApplicationTest {

    private fun getAuthToken(engine: TestApplicationEngine): String {

        return with(engine.handleRequest(HttpMethod.Post, "/api/v1/authentication") {
            addHeader(HttpHeaders.ContentType, Json.toString())
            setBody(
                """
                        {
                        "username": "admin",
                        "password": "admin"
                        }
                    """.trimIndent()
            )
        }) {
            response
            assertEquals(HttpStatusCode.OK, response.status())
            JsonPath.read<String>(response.content!!, "$.token")
        }

    }

    @Test
    fun testPostsOnlyAuthorized() {

        withTestApplication({ module() }) {
            with(handleRequest(HttpMethod.Get, "/api/v1/posts")) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

    }

    @Test
    fun testRegistration() {

        withTestApplication({ module() }) {

            with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                addHeader(HttpHeaders.ContentType, Json.toString())
                setBody(
                    """
                        {
                        "username": "newUser",
                        "password": "12346578"
                        }
                    """.trimIndent()
                )
            }) {
                response
                assertEquals(HttpStatusCode.NoContent, response.status())
            }

        }

    }

    @Test
    fun testAuth() {

        withTestApplication({ module() }) {

            val token = getAuthToken(this)

            with(handleRequest(HttpMethod.Get, "/api/v1/profile") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }) {
                response
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }

    }

    @Test
    fun testGetAll() {

        withTestApplication({ module() }) {

            val token = getAuthToken(this)

            with(handleRequest(HttpMethod.Get, "/api/v1/posts") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }) {
                response
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }

    }

}