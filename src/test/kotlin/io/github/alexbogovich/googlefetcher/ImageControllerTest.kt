package io.github.alexbogovich.googlefetcher

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ImageControllerTest {

    @Test
    fun getImage() {
        println(image.getImage("ru", ImageSearchReq("aws")))
    }

    companion object {
        private val image = ImageController(ObjectMapper())
    }
}
