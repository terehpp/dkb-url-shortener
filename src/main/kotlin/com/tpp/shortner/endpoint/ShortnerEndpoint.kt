package com.tpp.shortner.endpoint

import com.tpp.shortner.service.UrlService
import com.tpp.shortner.validation.ValidUrl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.MalformedURLException
import javax.servlet.http.HttpServletResponse


@RestController
@Validated
class ShortnerEndpoint(val urlService: UrlService) {
    @PutMapping("/make")
    fun make(@ValidUrl @RequestBody url: String): String {
        return urlService.put(url)
    }

    @GetMapping("/{short}")
    fun redirect(@PathVariable("short") shortUrl: String, response: HttpServletResponse) {
        val url = urlService.get(shortUrl)
        if ((url == null) || url.isBlank()) {
            response.sendError(HttpStatus.NOT_FOUND.value())
        } else {
            response.sendRedirect(url);
        }
    }

    @ExceptionHandler(MalformedURLException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(e: MalformedURLException): ResponseEntity<String?>? {
        return ResponseEntity("URL is not valid: " + e.message, HttpStatus.BAD_REQUEST)
    }
}