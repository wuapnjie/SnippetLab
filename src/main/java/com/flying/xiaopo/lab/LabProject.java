package com.flying.xiaopo.lab;

import com.intellij.openapi.project.Project;

/**
 * @author wupanjie on 2017/11/5.
 */
public class LabProject {

  private Project project;

  private LabProject() {
    //no instance
  }

  public static LabProject getInstance(){
    return LazyLoad.lazy;
  }

  public void install(Project project){
    this.project = project;
  }

  public Project getProject() {
    return project;
  }

  static class LazyLoad{
    static LabProject lazy = new LabProject();
  }
}
