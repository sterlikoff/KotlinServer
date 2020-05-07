import com.jayway.jsonpath.JsonPath
import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.content.PartData
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import kotlinx.io.streams.asInput
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ApplicationTest {

    private val uploadPath = Files.createTempDirectory("test").toString()

    private val configure: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).apply {
            put("upload.dir", uploadPath)
        }
        module()
    }

    private fun getAuthToken(engine: TestApplicationEngine, username: String = "admin", password: String = "admin"): String {

        return with(engine.handleRequest(HttpMethod.Post, "/api/v1/authentication") {
            addHeader(HttpHeaders.ContentType, Json.toString())
            setBody(
                """
                        {
                        "username": "$username",
                        "password": "$password"
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

        withTestApplication(configure) {
            with(handleRequest(HttpMethod.Get, "/api/v1/posts")) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
            }
        }

    }

    @Test
    fun testRegistration() {

        withTestApplication(configure) {

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

        withTestApplication(configure) {

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

        withTestApplication(configure) {

            val token = getAuthToken(this)

            with(handleRequest(HttpMethod.Get, "/api/v1/posts") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }) {
                response
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }

    }

    @Test
    fun testPostActions() {

        withTestApplication(configure) {

            val token = getAuthToken(this)

            with(handleRequest(HttpMethod.Post, "/api/v1/posts/create") {

                addHeader(HttpHeaders.Authorization, "Bearer $token")
                addHeader(HttpHeaders.ContentType, Json.toString())
                setBody(
                    """
                        {
                        "title": "title of post",
                        "content": "content of post"
                        }
                    """.trimIndent()
                )

            }) {
                response

                assertEquals(HttpStatusCode.OK, response.status())

                val title = JsonPath.read<String>(response.content!!, "$.title")
                val content = JsonPath.read<String>(response.content!!, "$.content")
                val id = JsonPath.read<Int>(response.content!!, "$.id")

                assertEquals(title, "title of post")
                assertEquals(content, "content of post")

                with(handleRequest(HttpMethod.Get, "/api/v1/posts/like/$id") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token")
                }) {
                    response

                    assertEquals(HttpStatusCode.OK, response.status())

                    val likeCount = JsonPath.read<Int>(response.content!!, "$.likeCount")
                    assertEquals(likeCount, 1)

                    with(handleRequest(HttpMethod.Get, "/api/v1/posts/dislike/$id") {
                        addHeader(HttpHeaders.Authorization, "Bearer $token")
                    }) {
                        response

                        assertEquals(HttpStatusCode.OK, response.status())

                        val newLikeCount = JsonPath.read<Int>(response.content!!, "$.likeCount")
                        assertEquals(newLikeCount, 0)

                    }

                }

                with(handleRequest(HttpMethod.Get, "/api/v1/posts/share/$id") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token")
                }) {
                    response

                    assertEquals(HttpStatusCode.OK, response.status())
                    val parentId = JsonPath.read<Int>(response.content!!, "$.parentId")

                    assertEquals(parentId, id)

                }

            }

        }

    }

    @Test
    fun testAlien() {

        withTestApplication(configure) {

            val token = getAuthToken(this)

            with(handleRequest(HttpMethod.Delete, "/api/v1/posts/1") {
                addHeader(HttpHeaders.Authorization, "Bearer $token")
            }) {
                response
                assertEquals(HttpStatusCode.Forbidden, response.status())
            }

        }

    }

    @Test
    fun testUpload() {
        withTestApplication(configure) {

            val token = getAuthToken(this, "user", "user")

            with(handleRequest(HttpMethod.Post, "/api/v1/posts/addImage/1") {

                val boundary = "***bbb***"

                addHeader(HttpHeaders.Authorization, "Bearer $token")
                addHeader(HttpHeaders.ContentType, ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString())

                setBody(
                    boundary,
                    listOf(
                        PartData.FileItem({
                            Files.newInputStream(Paths.get("./src/test/resources/test.jpg")).asInput()
                        }, {}, headersOf(
                            HttpHeaders.ContentDisposition to listOf(
                                ContentDisposition.File.withParameter(
                                    ContentDisposition.Parameters.Name, "file"
                                ).toString(),
                                ContentDisposition.File.withParameter(
                                    ContentDisposition.Parameters.FileName, "photo.jpg"
                                ).toString()
                            ),
                            HttpHeaders.ContentType to listOf(ContentType.Image.JPEG.toString())
                        )
                        )
                    )
                )
            }) {
                assertEquals(HttpStatusCode.OK, response.status())

                val postImageId = JsonPath.read<String>(response.content!!, "$.imageId")
                assertTrue(postImageId != null)

            }
        }
    }

}