package net.brew.cdk.app

import software.amazon.awscdk.App
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.Tags

fun main() {
    val app = App()
    ApplicationStack(app, "app-runner-kotlin", StackProps.builder().build())
    Tags.of(app).add("project", "app-runner-kotlin-demo")
    Tags.of(app).add("createdBy", "Michael Brewer")
    app.synth()
}
