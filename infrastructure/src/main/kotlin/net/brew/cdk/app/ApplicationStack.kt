package net.brew.cdk.app

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apprunner.alpha.ConfigurationSourceType
import software.amazon.awscdk.services.apprunner.alpha.GitHubConnection
import software.amazon.awscdk.services.apprunner.alpha.GithubRepositoryProps
import software.amazon.awscdk.services.apprunner.alpha.Service
import software.amazon.awscdk.services.apprunner.alpha.ServiceProps
import software.amazon.awscdk.services.apprunner.alpha.Source
import software.constructs.Construct

class ApplicationStack(scope: Construct, id: String, props: StackProps) : Stack(scope, id, props) {
    init {
        Service(
            this,
            "AppRunnerKotlinDemo",
            ServiceProps
                .builder()
                .serviceName("KotlinService")
                .source(Source.fromGitHub(
                    GithubRepositoryProps
                        .builder()
                        .repositoryUrl("https://github.com/michaelbrewer/app-runner-kotlin")
                        .branch("main")
                        .connection(GitHubConnection.fromConnectionArn(System.getenv("GITHUB_ARN")))
                        .configurationSource(ConfigurationSourceType.REPOSITORY)
                        .build()
                ))
                .build()
        )
    }
}
