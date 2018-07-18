package tutorial;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.builders.task.ArtifactDownloaderTask;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.DownloadItem;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;

@BambooSpec
public class DepSpec {
    
    public Deployment rootObject() {
        final Deployment rootObject = new Deployment(new PlanIdentifier("MYD", "MYZ"),
            "Deployment for MyDev1")
            .description("Deployment for MyDev1")
            .releaseNaming(new ReleaseNaming("release-1")
                    .autoIncrement(true))
            .environments(new Environment("Dev1")
                    .description("This is Dev1 environment")
                    .tasks(new CleanWorkingDirectoryTask()
                            .description("Clean working dir"),
                        new ArtifactDownloaderTask()
                            .description("Download war file")
                            .artifacts(new DownloadItem()
                                    .artifact("MyWARFile")
                                    .path("Final")),
                        new MavenTask()
                            .description("list apps")
                            .goal("com.oracle.weblogic:weblogic-maven-plugin:list-apps -Duser=${bamboo.weblogic.user} -Dpassword=${bamboo.weblogic.password}  -Dadminurl=${bamboo.weblogic.adminurl}")
                            .jdk("JDK 1.8")
                            .executableLabel("Maven 3"))
                    .triggers(new AfterSuccessfulBuildPlanTrigger()
                            .description("App deploy trigger")));
        return rootObject;
    }
    
    public DeploymentPermissions deploymentPermission() {
        final DeploymentPermissions deploymentPermission = new DeploymentPermissions("Deployment for MyDev1")
            .permissions(new Permissions()
                    .userPermissions("gkumar", PermissionType.EDIT, PermissionType.VIEW)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return deploymentPermission;
    }
    
    public EnvironmentPermissions environmentPermission1() {
        final EnvironmentPermissions environmentPermission1 = new EnvironmentPermissions("Deployment for MyDev1")
            .environmentName("Dev1")
            .permissions(new Permissions()
                    .userPermissions("gkumar", PermissionType.EDIT, PermissionType.VIEW, PermissionType.BUILD)
                    .loggedInUserPermissions(PermissionType.VIEW)
                    .anonymousUserPermissionView());
        return environmentPermission1;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("http://localhost:8085");
        final DepSpec depSpec = new DepSpec();
        
        final Deployment rootObject = depSpec.rootObject();
        bambooServer.publish(rootObject);
        
        final DeploymentPermissions deploymentPermission = depSpec.deploymentPermission();
        bambooServer.publish(deploymentPermission);
        
        final EnvironmentPermissions environmentPermission1 = depSpec.environmentPermission1();
        bambooServer.publish(environmentPermission1);
    }
}