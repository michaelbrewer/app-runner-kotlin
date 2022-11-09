package net.brew.cdk.app

import software.amazon.awscdk.App
import software.amazon.awscdk.StackProps

fun main() {
    val app = App()
    ApplicationStack(app, "app-runner-kotlin", StackProps.builder().build())
    app.synth()
}