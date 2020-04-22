package services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

class JWTTokenService {

    private val secret = "5c2dbef6-289c-46e6-8cfd-d8b3292d373a"
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun generate(id: Int): String = JWT.create()
        .withClaim("id", id)
        .sign(algorithm)

}