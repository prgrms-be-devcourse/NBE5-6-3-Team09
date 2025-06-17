package com.codemap.mail.app.model.code

enum class MailTemplatePath(val path:String) {
    SIGNUP_VERIFY("/mail/signup-verification"),
    SIGNUP_COMPLETE("/mail/signup-complete"),
    PASSWORD_RESET("/mail/password-reset")
}