package org.ming.app;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        URI jenkinsUrl = null;

        try {
            jenkinsUrl = new URI("http://localhost:8080");
        } catch(URISyntaxException e) {
            System.out.println("url is error");
        }

        String user = "admin";
        String passWord = "118e9013e36e897cd566c2b1dbccc4a489";

        JenkinsServer jenkins = new JenkinsServer(jenkinsUrl, user, passWord);

        long retryInterval = 500;

        String jobName = "test";

        System.out.println("running");

        String buildUrl = null;

        try{

            JobWithDetails build_job = jenkins.getJob(jobName);
            QueueReference queueRef = build_job.build();
            System.out.println("building");
            JobWithDetails job = jenkins.getJob(jobName);

            QueueItem queueItem;
            for(queueItem = jenkins.getQueueItem(queueRef); !queueItem.isCancelled() && job.isInQueue(); queueItem = jenkins.getQueueItem(queueRef)) {
                Thread.sleep(retryInterval);
                job = jenkins.getJob(jobName);
            }

            Build build = jenkins.getBuild(queueItem);
            if (queueItem.isCancelled()) {
                 System.out.println(build.details());
            } else {
                System.out.println("get output");
                buildUrl = build.getUrl();
                BuildWithDetails buildWithDetails = build.details();
                int poolingInterval = 1;
                int bufferOffset = 0;

                while(true) {
                    Thread.sleep((long)(poolingInterval * 1000));
                    ConsoleLog consoleLog = null;
                    consoleLog = buildWithDetails.getConsoleOutputText(bufferOffset);
                    String logString = consoleLog.getConsoleLog();
                    if (logString != null && logString.length() != 0) {
                        System.out.println(logString);
                    }

                    if (consoleLog.getHasMoreData()) {
                        bufferOffset = consoleLog.getCurrentBufferSize();
                        BuildWithDetails current_build = build.details();
                        if (current_build.isBuilding()) {
                            continue;
                        }
                        break;
                    }
                    break;
                }
                buildWithDetails = build.details();
                BuildResult buildResult = buildWithDetails.getResult();
                String status = buildResult.name();
                if (status.equalsIgnoreCase("SUCCESS")) {
                    System.exit(0);
                } else {
                    System.exit(1);
                }

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Detail build log please refer to " + buildUrl);
        } catch (InterruptedException e1) {
            System.out.println(e1.getMessage());
            System.out.println("Detail build log please refer to " + buildUrl);
        }


    }
}
