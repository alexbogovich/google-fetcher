package io.github.alexbogovich.googlefetcher

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder


@SpringBootApplication
@EnableCaching
class GoogleFetcherApplication

fun main(args: Array<String>) {
    runApplication<GoogleFetcherApplication>(*args)
}

@RestController
@RequestMapping("/api/image")
class ImageController(private val objectMapper: ObjectMapper) {

    @PostMapping()
    @Cacheable("getImage")
    fun getImage(@RequestParam(defaultValue = "en") hl: String, @RequestBody q: SearchReq): List<ImageSearchRes> {
        val encodedQuery = URLEncoder.encode(q.search, "UTF-8")
        return Jsoup.connect("https://www.google.com/search?tbm=isch&q=$encodedQuery}&hl=$hl")
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
            .get()
            .select(".rg_meta")
            .map {
                objectMapper.readValue(it.text(), ImageJson::class.java).toRes()
            }
    }
}

@RestController
@RequestMapping("/api/title")
class TitleController {
    @PostMapping()
    @Cacheable("getTitle")
    fun getTitle(@RequestParam(defaultValue = "en") hl: String, @RequestBody q: SearchReq): List<TitleSearchRes> {
        val encodedQuery = URLEncoder.encode(q.search, "UTF-8")
        return Jsoup.connect("https://www.google.com/search?q=$encodedQuery&hl=$hl")
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
            .get()
            .select(".rc .r a h3")
            .map { TitleSearchRes(it.text()) }
    }
}

data class SearchReq(val search: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageJson(val ou: String = "", val oh: String = "", val ow: String = "", val pt: String = "") {
    fun toRes(): ImageSearchRes = ImageSearchRes(ou, oh, ow, pt)
}

data class ImageSearchRes(val url: String, val height: String, val width: String, val text: String)

data class TitleSearchRes(val title: String? = null)

fun String?.toTitleRes(): TitleSearchRes {
    return if (this.isNullOrEmpty()) {
        TitleSearchRes()
    } else {
        TitleSearchRes(this)
    }
}
