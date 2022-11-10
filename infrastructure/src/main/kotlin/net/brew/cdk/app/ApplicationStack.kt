package net.brew.cdk.app

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apprunner.CfnObservabilityConfiguration
import software.amazon.awscdk.services.apprunner.CfnObservabilityConfigurationProps
import software.amazon.awscdk.services.apprunner.CfnService
import software.amazon.awscdk.services.apprunner.CfnServiceProps
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.Role
import software.amazon.awscdk.services.iam.RoleProps
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.constructs.Construct

class ApplicationStack(scope: Construct, id: String, props: StackProps) : Stack(scope, id, props) {
    init {
        // Handy L2 construct is not complete
//        Service(
//            this,
//            "AppRunnerKotlinDemo",
//            ServiceProps
//                .builder()
//                .serviceName("KotlinService")
//                .source(Source.fromGitHub(
//                    GithubRepositoryProps
//                        .builder()
//                        .repositoryUrl("https://github.com/michaelbrewer/app-runner-kotlin")
//                        .branch("main")
//                        .connection(GitHubConnection.fromConnectionArn(System.getenv("GITHUB_ARN")))
//                        .configurationSource(ConfigurationSourceType.REPOSITORY)
//                        .build()
//                ))
//                .build()
//        )

        // Much more verbose L1 version to support setting observability
        val instanceRole = Role(
            this,
            "AppRunnerKotlinRole",
            RoleProps
                .builder()
                .assumedBy(ServicePrincipal("tasks.apprunner.amazonaws.com"))
                .description("Create App Runner instance access to x-ray")
                .managedPolicies(listOf(ManagedPolicy.fromAwsManagedPolicyName("AWSXRayDaemonWriteAccess")))
                .build()
        )
        val observabilityConfiguration = CfnObservabilityConfiguration(
            this,
            "AppRunnerObservability",
            CfnObservabilityConfigurationProps.builder()
                .observabilityConfigurationName("AppRunnerObservability")
                .traceConfiguration(
                    CfnObservabilityConfiguration.TraceConfigurationProperty
                        .builder()
                        .vendor("AWSXRAY")
                        .build()
                ).build()
        )
        CfnService(
            this,
            "AppRunnerKotlinDemo",
            CfnServiceProps
                .builder()
                .sourceConfiguration(
                    CfnService.SourceConfigurationProperty
                        .builder()
                        .authenticationConfiguration(CfnService.AuthenticationConfigurationProperty
                            .builder()
                            .connectionArn(System.getenv("GITHUB_ARN"))
                            .build()
                        )
                        .codeRepository(CfnService.CodeRepositoryProperty
                            .builder()
                            .codeConfiguration(
                                CfnService.CodeConfigurationProperty
                                    .builder()
                                    .configurationSource("REPOSITORY")
                                    .build()
                            )
                            .repositoryUrl("https://github.com/michaelbrewer/app-runner-kotlin")
                            .sourceCodeVersion(CfnService.SourceCodeVersionProperty
                                .builder()
                                .type("BRANCH")
                                .value("main")
                                .build()
                            )
                            .build()
                        )
                        .build()
                )
                .instanceConfiguration(CfnService.InstanceConfigurationProperty
                    .builder()
                    .instanceRoleArn(instanceRole.roleArn)
                    .build()
                )
                .networkConfiguration(
                    CfnService.NetworkConfigurationProperty
                        .builder()
                        .egressConfiguration(
                            CfnService.EgressConfigurationProperty.builder().egressType("DEFAULT").build()
                        ).build()
                )
                .serviceName("KotlinService")
                .observabilityConfiguration(
                    CfnService.ServiceObservabilityConfigurationProperty
                        .builder()
                        .observabilityConfigurationArn(observabilityConfiguration.attrObservabilityConfigurationArn)
                        .observabilityEnabled(true)
                        .build()
                )
                .build()
        )
    }
}
