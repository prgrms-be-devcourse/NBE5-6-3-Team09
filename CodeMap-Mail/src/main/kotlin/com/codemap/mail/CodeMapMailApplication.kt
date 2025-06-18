package com.codemap.mail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CodeMapMailApplication

fun main(args: Array<String>) {
    runApplication<CodeMapMailApplication>(*args)
}