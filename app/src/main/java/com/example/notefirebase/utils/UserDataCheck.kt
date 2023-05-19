package com.example.notefirebase.utils

class UserDataCheck(
    private val password: String,
    private val email: String
) {
    private val MIN_PASSWORD_LENGTH = 8

    // Checking the length of the password
    fun checkPasswordLength(): Boolean {
        if (password.length < MIN_PASSWORD_LENGTH) {
            return true
        }
        return false
    }

    // Checking for uppercase and lowercase letters
    fun checkPasswordUpLow(): Boolean {
        if (!containsUppercase(password) || !containsLowercase(password)) {
            return true
        }
        return false
    }

    // Checking which language password consists of
    fun checkPasswordEngCh(): Boolean {
        if (isEnglishLetters(password)) {
            return true
        }
        return false
    }

    // Method for checking for the content of capital letters
    private fun containsUppercase(str: String): Boolean {
        for (ch in str) {
            if (ch.isUpperCase()) {
                return true
            }
        }
        return false
    }

    // Method for checking for the content of lowercase letters
    private fun containsLowercase(str: String): Boolean {
        for (ch in str) {
            if (ch.isLowerCase()) {
                return true
            }
        }
        return false
    }

    // Method for checking for the content of English characters
    private fun isEnglishLetters(str: String): Boolean {
        if(str.matches(Regex("^[a-zA-Z0-9]+\$")))
            return true
        return false
    }

    // Method for checking the validity of email
    fun isEmailValid(): Boolean {
        if (email.isEmpty()) return false

        if (!email.contains("@")) return false

        val parts = email.split("@")
        if (parts.size != 2) return false

        val username = parts[0]
        val domain = parts[1]
        if (username.isBlank() || domain.isBlank()) return false

        if (domain.count { it == '.' } < 1) return false

        if (!isValidDomain(domain)) return false

        if (email.contains(" ")) return false

        if (email.length > 254) return false
        return true
    }

    // Method for checking the validity of domain
    private fun isValidDomain(domain: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9.-]+$")
        if (!regex.matches(domain)) return false
        return domain.split(".").all { it.length in 1..63 }
    }
}