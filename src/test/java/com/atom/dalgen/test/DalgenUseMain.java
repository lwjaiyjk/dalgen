/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.atom.dalgen.test;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * 任务测试
 * 
 * @author obullxl@gmail.com
 * @version $Id: DalgenUseMain.java, V1.0.1 2013年11月22日 下午2:32:14 $
 */
public final class DalgenUseMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Project project = new Project();
        project.init();
        
        DefaultLogger logger = new DefaultLogger();
        logger.setErrorPrintStream(System.err);
        logger.setOutputPrintStream(System.out);
        logger.setMessageOutputLevel(Project.MSG_DEBUG);
        project.addBuildListener(logger);

        String path = new File(".").getAbsolutePath();
        String file = FilenameUtils.normalize(path + "/dalgen/build.xml");

        ProjectHelper helper = ProjectHelper.getProjectHelper();
        helper.parse(project, new File(file));
        
        project.executeTarget(project.getDefaultTarget());
    }

}
