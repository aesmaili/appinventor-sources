// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2015 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.shared.rpc.component;

import com.google.appinventor.shared.rpc.ServerLayout;
import com.google.appinventor.shared.rpc.project.ProjectNode;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

@RemoteServiceRelativePath(ServerLayout.COMPONENT_SERVICE)
public interface ComponentService extends RemoteService {

  /**
   * @return A list of info of user's components
   */
  List<ComponentInfo> getComponentInfos();

  /**
   * Import the component to the project in the server and
   * return a list of ProjectNode that can be added to the client
   *
   * @param info info about the component
   * @param projectId id of the project to which the component will be added
   * @param folderPath folder to which the component will be stored
   * @return a list of ProjectNode created from the component
   */
  List<ProjectNode> importComponentToProject(ComponentInfo info, long projectId, String folderPath);

}