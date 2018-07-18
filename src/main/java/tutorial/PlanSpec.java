package tutorial;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.repository.VcsChangeDetection;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.api.builders.task.AnyTask;
import com.atlassian.bamboo.specs.builders.repository.git.UserPasswordAuthentication;
import com.atlassian.bamboo.specs.builders.repository.github.GitHubRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.GitHubRepositoryViewer;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RemoteTrigger;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.util.MapBuilder;
import java.time.Duration;

@BambooSpec
public class PlanSpec {
    
    public Plan plan() {
        final Plan plan = new Plan(new Project()
                /*.oid(new BambooOid("cxx7l5poipz5"))*/
                .key(new BambooKey("MYD"))
                .name("MyDev")
                .description("Dev project for testing CICD"),
            "MyPlan1",
            new BambooKey("MYZ"))
            /*.oid(new BambooOid("cxnidkcgoxkx"))*/
            .description("Plan for implementing CICD 1")
            .pluginConfigurations(new ConcurrentBuilds()
                    .useSystemWideDefault(false))
            .stages(new Stage("Continuous Integration")
                    .jobs(new Job("CI Job",
                            new BambooKey("JOB1"))
                            .artifacts(new Artifact()
                                    .name("MyWARFile")
                                    .copyPattern("*.war")
                                    .location("target")
                                    .shared(true),
                                new Artifact()
                                    .name("Cobertura Report")
                                    .copyPattern("*")
                                    .location("target/site/cobertura"))
                            .tasks(new CleanWorkingDirectoryTask()
                                    .description("Clean working directory"),
                                new VcsCheckoutTask()
                                    .description("Checkout GitHub Repo")
                                    .checkoutItems(new CheckoutItem()
                                            .repository(new VcsRepositoryIdentifier()
                                                    .name("sherlock")))
                                    .cleanCheckout(true),
                                new MavenTask()
                                    .description("Maven with Cobertura CC report")
                                    .goal("cobertura:cobertura -Dcobertura.report.format=xml")
                                    .jdk("JDK 1.8")
                                    .executableLabel("Maven 3"),
                                new AnyTask(new AtlassianModule("ch.mibex.bamboo.sonar4bamboo:sonar4bamboo.maven3task"))
                                    .description("Sonar Report Publisher")
                                    .enabled(false)
                                    .configuration(new MapBuilder()
                                            .put("incrementalFileForInclusionList", "")
                                            .put("chosenSonarConfigId", "1")
                                            .put("useGradleWrapper", "")
                                            .put("useNewGradleSonarQubePlugin", "")
                                            .put("sonarJavaSource", "")
                                            .put("sonarProjectName", "")
                                            .put("buildJdk", "JDK 1.8")
                                            .put("gradleWrapperLocation", "")
                                            .put("sonarLanguage", "")
                                            .put("sonarSources", "")
                                            .put("useGlobalSonarServerConfig", "true")
                                            .put("incrementalMode", "")
                                            .put("failBuildForBrokenQualityGates", "")
                                            .put("sonarTests", "")
                                            .put("incrementalNoPullRequest", "incrementalModeFailBuildField")
                                            .put("failBuildForSonarErrors", "")
                                            .put("sonarProjectVersion", "")
                                            .put("sonarBranch", "")
                                            .put("executable", "Maven 3")
                                            .put("illegalBranchCharsReplacement", "_")
                                            .put("failBuildForTaskErrors", "true")
                                            .put("incrementalModeNotPossible", "incrementalModeRunFullAnalysis")
                                            .put("sonarJavaTarget", "")
                                            .put("environmentVariables", "")
                                            .put("incrementalModeGitBranchPattern", "")
                                            .put("legacyBranching", "true")
                                            .put("replaceSpecialBranchChars", "")
                                            .put("additionalProperties", "")
                                            .put("autoBranch", "true")
                                            .put("sonarProjectKey", "")
                                            .put("incrementalModeBambooUser", "")
                                            .put("overrideSonarBuildConfig", "")
                                            .put("workingSubDirectory", "")
                                            .build()),
                                new MavenTask()
                                    .description("Build task")
                                    .enabled(false)
                                    .goal("clean compile package")
                                    .jdk("JDK 1.8")
                                    .executableLabel("Maven 3"))))
            .linkedRepositories("first-project")
            .planRepositories(new GitHubRepository()
                    .name("sherlock")
                   /* .oid(new BambooOid("cxro1e2p6vwh"))*/
                    .repositoryViewer(new GitHubRepositoryViewer())
                    .repository("Gpkmr/sherlock")
                    .branch("master")
                    .authentication(new UserPasswordAuthentication("gpkmr")
                            .password("Gopi@16!"))
                    .changeDetection(new VcsChangeDetection()))
            
            .triggers(new RepositoryPollingTrigger()
                    .enabled(false)
                    .withPollingPeriod(Duration.ofSeconds(30)),
                new RemoteTrigger()
                    .description("GitHub trigger")
                    .enabled(false))
            .planBranchManagement(new PlanBranchManagement()
                    .delete(new BranchCleanup())
                    .notificationForCommitters());
        return plan;
    }
    
    public PlanPermissions planPermission() {
        final PlanPermissions planPermission = new PlanPermissions(new PlanIdentifier("MYD", "MYZ"))
            .permissions(new Permissions()
                    .userPermissions("gkumar", PermissionType.EDIT, PermissionType.VIEW, PermissionType.ADMIN, PermissionType.CLONE, PermissionType.BUILD)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return planPermission;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://localhost:8085");
        final PlanSpec planSpec = new PlanSpec();
        
        final Plan plan = planSpec.plan();
        bambooServer.publish(plan);
        
        final PlanPermissions planPermission = planSpec.planPermission();
        bambooServer.publish(planPermission);
    }
}