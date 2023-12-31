plugins {
    id("com.gradle.enterprise") version "3.16.1"
}

if (System.getenv("CI") != null) {
    buildScan {
        publishAlways()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}