package com.easychat.config;

import com.easychat.utils.StringTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {
    @Value("${ws.port:}")
    private Integer wePort;
    @Value("${project.folder:}")
    private String projectFolder;
    @Value("${admin.emails:}")
    private String adminEmails;

    public Integer getWePort() {
        return wePort;
    }

    public String getProjectFolder() {
        if(StringTools.isEmpty(projectFolder)&&!projectFolder.endsWith("/")){
            projectFolder=projectFolder+"/";

        }
        return projectFolder;
    }

    public String getAdminEmails() {
        return adminEmails;
    }
}
