// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2013 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client;

import com.google.appinventor.client.boxes.ProjectListBox;
import com.google.appinventor.client.boxes.ViewerBox;
import com.google.appinventor.client.explorer.commands.*;
import com.google.appinventor.client.explorer.project.Project;
import com.google.appinventor.client.output.OdeLog;
import com.google.appinventor.client.tracking.Tracking;
import com.google.appinventor.client.utils.Downloader;
import com.google.appinventor.client.widgets.DropDownButton;
import com.google.appinventor.client.widgets.DropDownButton.DropDownItem;
import com.google.appinventor.client.wizards.KeystoreUploadWizard;
import com.google.appinventor.client.wizards.ProjectUploadWizard;
import com.google.appinventor.client.wizards.youngandroid.NewYoungAndroidProjectWizard;
import com.google.appinventor.common.version.AppInventorFeatures;
import com.google.appinventor.common.version.GitBuildId;
import com.google.appinventor.shared.rpc.ServerLayout;
import com.google.appinventor.shared.rpc.project.ProjectRootNode;
import com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidProjectNode;
import com.google.appinventor.shared.storage.StorageUtil;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.List;

import static com.google.appinventor.client.Ode.MESSAGES;


/**
 * TopToolbar lives in the TopPanel, to create functionality in the designer.
 */
public class TopToolbar extends Composite {
  private static final String LEARN_URL = Ode.APP_INVENTOR_DOCS_URL + "/learn/";
  private static final String KNOWN_ISSUES_LINK_URL =
      Ode.APP_INVENTOR_DOCS_URL + "/knownIssues.html";
  private static final String RELEASE_NOTES_LINK_URL =
      Ode.APP_INVENTOR_DOCS_URL + "/ReleaseNotes.html";
  private static final String KNOWN_ISSUES_LINK_AND_TEXT =
      "<a href=\"" + KNOWN_ISSUES_LINK_URL + "\" target=\"_blank\">known issues</a>" ;
  private static final String RELEASE_NOTES_LINK_AND_TEXT =
      "<a href=\"" + RELEASE_NOTES_LINK_URL + "\" target=\"_blank\">release notes</a>" ;
  private static final String GALLERY_LINK_AND_TEXT =
      "<a href=\"http://gallery.appinventor.mit.edu\" target=\"_blank\">" +
          "Try the App Inventor Community Gallery (Beta)</a>";
  private static final String termsOfServiceText =
      "<a href='" + Ode.APP_INVENTOR_DOCS_URL + "/about/termsofservice.html'" +
          " target=_blank>" + MESSAGES.privacyTermsLink() + "</a>";

  private static final String WIDGET_NAME_NEW = "New";
  private static final String WIDGET_NAME_DELETE = "Delete";
  private static final String WIDGET_NAME_DOWNLOAD_SOURCE = "DownloadSource";
  private static final String WIDGET_NAME_UPLOAD_SOURCE = "UploadSource";
  private static final String WIDGET_NAME_ADMIN = "Admin";
  private static final String WIDGET_NAME_DOWNLOAD_USER_SOURCE = "DownloadUserSource";
  private static final String WIDGET_NAME_DOWNLOAD_KEYSTORE = "DownloadKeystore";
  private static final String WIDGET_NAME_UPLOAD_KEYSTORE = "UploadKeystore";
  private static final String WIDGET_NAME_DELETE_KEYSTORE = "DeleteKeystore";
  private static final String WIDGET_NAME_SAVE = "Save";
  private static final String WIDGET_NAME_SAVE_AS = "SaveAs";
  private static final String WIDGET_NAME_SAVE_OPTIONS = "SaveOptions";
  private static final String WIDGET_NAME_CHECKPOINT = "Checkpoint";
  private static final String WIDGET_NAME_MY_PROJECTS = "MyProjects";
  private static final String WIDGET_NAME_BUILD = "Build";
  private static final String WIDGET_NAME_BUILD_BARCODE = "Barcode";
  private static final String WIDGET_NAME_BUILD_DOWNLOAD = "Download";
  private static final String WIDGET_NAME_BUILD_YAIL = "Yail";
  private static final String WIDGET_NAME_SCREENS_DROPDOWN = "ScreensDropdown";
  private static final String WIDGET_NAME_SWITCH_TO_BLOCKS_EDITOR = "SwitchToBlocksEditor";
  private static final String WIDGET_NAME_SWITCH_TO_FORM_EDITOR = "SwitchToFormEditor";
  private static final String WIDGET_NAME_CONNECT_TO = "ConnectTo";
  private static final String WIDGET_NAME_WIRELESS_BUTTON = "Wireless";
  private static final String WIDGET_NAME_EMULATOR_BUTTON = "Emulator";
  private static final String WIDGET_NAME_USB_BUTTON = "Usb";
  private static final String WIDGET_NAME_RESET_BUTTON = "Reset";
  private static final String WIDGET_NAME_FILE = "File";
  private static final String WIDGET_NAME_HELP = "Help";
  private static final String WIDGET_NAME_ABOUT = "About";
  private static final String WIDGET_NAME_LIBRARY = "Library";
  private static final String WIDGET_NAME_GETSTARTED = "GetStarted";
  private static final String WIDGET_NAME_TUTORIALS = "Tutorials";
  private static final String WIDGET_NAME_TROUBLESHOOTING = "Troubleshooting";
  private static final String WIDGET_NAME_FORUMS = "Forums";
  private static final String WIDGET_NAME_FEEDBACK = "ReportIssue";
  private static final String WIDGET_NAME_IMPORTPROJECT = "ImportProject";
  private static final String WIDGET_NAME_EXPORTALLPROJECTS = "ExportAllProjects";
  private static final String WIDGET_NAME_EXPORTPROJECT = "ExportProject";

  private DropDownButton fileDropDown;
  private DropDownButton connectDropDown;
  private DropDownButton buildDropDown;
  private DropDownButton helpDropDown;

  public TopToolbar() {
    /*
     * Layout is as follows:
     * +--------------------------------------+
     * | File ▾ | Connect ▾ | Build ▾| Help ▾ |
     * +--------------------------------------+
     */
    HorizontalPanel toolbar = new HorizontalPanel();
    toolbar.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

    List<DropDownItem> fileItems = Lists.newArrayList();
    List<DropDownItem> connectItems = Lists.newArrayList();
    List<DropDownItem> buildItems = Lists.newArrayList();
    List<DropDownItem> helpItems = Lists.newArrayList();

    // File -> {New Project; Save; Save As; Checkpoint; |; Delete this Project; My Projects;}
    fileItems.add(new DropDownItem(WIDGET_NAME_MY_PROJECTS, MESSAGES.tabNameProjects(),
        new SwitchToProjectAction()));
    fileItems.add(null);
    fileItems.add(new DropDownItem(WIDGET_NAME_NEW, MESSAGES.newButton(),
        new NewAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_DELETE, MESSAGES.deleteProjectButton(),
        new DeleteAction()));
    fileItems.add(null);
    fileItems.add(new DropDownItem(WIDGET_NAME_SAVE, MESSAGES.saveButton(),
        new SaveAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_SAVE_AS, MESSAGES.saveAsButton(),
        new SaveAsAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_CHECKPOINT, MESSAGES.checkpointButton(),
        new CheckpointAction()));
    fileItems.add(null);
    fileItems.add(new DropDownItem(WIDGET_NAME_IMPORTPROJECT, MESSAGES.importProjectButton(),
        new ImportProjectAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_EXPORTPROJECT, MESSAGES.exportProjectButton(),
        new ExportProjectAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_EXPORTALLPROJECTS, MESSAGES.exportAllProjectsButton(),
        new ExportAllProjectsAction()));
    fileItems.add(null);
    fileItems.add(new DropDownItem(WIDGET_NAME_UPLOAD_KEYSTORE, MESSAGES.uploadKeystoreButton(),
        new UploadKeystoreAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_DOWNLOAD_KEYSTORE, MESSAGES.downloadKeystoreButton(),
        new DownloadKeystoreAction()));
    fileItems.add(new DropDownItem(WIDGET_NAME_DELETE_KEYSTORE, MESSAGES.deleteKeystoreButton(),
        new DeleteKeystoreAction()));

    // Connect -> {Connect to Companion; Connect to Emulator}
    connectItems.add(new DropDownItem(WIDGET_NAME_WIRELESS_BUTTON,
        MESSAGES.wirelessButton(), new WirelessAction()));
    connectItems.add(new DropDownItem(WIDGET_NAME_EMULATOR_BUTTON,
        MESSAGES.emulatorButton(), new EmulatorAction()));
    connectItems.add(new DropDownItem(WIDGET_NAME_USB_BUTTON, MESSAGES.usbButton(),
        new UsbAction()));
    connectItems.add(null);
    connectItems.add(new DropDownItem(WIDGET_NAME_RESET_BUTTON, MESSAGES.resetConnections(),
        new ResetAction()));

    // Build -> {Show Barcode; Download to Computer; Download Source; Download Keystore}
    buildItems.add(new DropDownItem(WIDGET_NAME_BUILD_BARCODE, MESSAGES.showBarcodeButton(),
        new BarcodeAction()));
    buildItems.add(new DropDownItem(WIDGET_NAME_BUILD_DOWNLOAD, MESSAGES.downloadToComputerButton(),
        new DownloadAction()));
    if (AppInventorFeatures.hasYailGenerationOption() && Ode.getInstance().getUser().getIsAdmin()) {
      buildItems.add(null);
      buildItems.add(new DropDownItem(WIDGET_NAME_BUILD_YAIL, MESSAGES.generateYailButton(),
          new GenerateYailAction()));
    }

    // Help -> {About, Guide}
    helpItems.add(new DropDownItem(WIDGET_NAME_ABOUT, MESSAGES.aboutLink(),
        new AboutAction()));
    helpItems.add(null);
    helpItems.add(new DropDownItem(WIDGET_NAME_LIBRARY, MESSAGES.libraryLink(),
        new LibraryAction()));
    helpItems.add(new DropDownItem(WIDGET_NAME_GETSTARTED, MESSAGES.getStartedLink(),
        new GetStartedAction()));
    helpItems.add(new DropDownItem(WIDGET_NAME_TUTORIALS, MESSAGES.tutorialsLink(),
        new TutorialsAction()));
    helpItems.add(new DropDownItem(WIDGET_NAME_TROUBLESHOOTING, MESSAGES.troubleshootingLink(),
        new TroubleShootingAction()));
    helpItems.add(new DropDownItem(WIDGET_NAME_FORUMS, MESSAGES.forumsLink(),
        new ForumsAction()));
    helpItems.add(null);
    helpItems.add(new DropDownItem(WIDGET_NAME_FEEDBACK, MESSAGES.feedbackLink(),
        new FeedbackAction()));

    // Create the DropDownButtons
    fileDropDown = new DropDownButton(WIDGET_NAME_FILE, MESSAGES.fileButton(),
        fileItems, false);
    connectDropDown = new DropDownButton(WIDGET_NAME_CONNECT_TO, MESSAGES.connectButton(),
        connectItems, false);
    buildDropDown = new DropDownButton(WIDGET_NAME_BUILD, MESSAGES.buildButton(),
        buildItems, false);
    helpDropDown = new DropDownButton(WIDGET_NAME_HELP, MESSAGES.helpLink(),
        helpItems, false);

    connectDropDown.setItemEnabled(MESSAGES.resetConnections(), false);
    updateFileMenuButtons(false);

    // Set the DropDown Styles
    fileDropDown.setStyleName("ode-TopPanelButton");
    connectDropDown.setStyleName("ode-TopPanelButton");
    buildDropDown.setStyleName("ode-TopPanelButton");
    helpDropDown.setStyleName("ode-TopPanelButton");

    // Add the Buttons to the Toolbar.
    toolbar.add(fileDropDown);
    toolbar.add(connectDropDown);
    toolbar.add(buildDropDown);
    toolbar.add(helpDropDown);

    initWidget(toolbar);
  }

  // -----------------------------
  // List of Commands for use in Drop-Down Menus
  // -----------------------------

  private static class NewAction implements Command {
    @Override
    public void execute() {
      new NewYoungAndroidProjectWizard().center();
      // The wizard will switch to the design view when the new
      // project is created.
    }
  }

  private class SaveAction implements Command {
    @Override
    public void execute() {
      ProjectRootNode projectRootNode = Ode.getInstance().getCurrentYoungAndroidProjectRootNode();
      if (projectRootNode != null) {
        ChainableCommand cmd = new SaveAllEditorsCommand(null);
        cmd.startExecuteChain(Tracking.PROJECT_ACTION_SAVE_YA, projectRootNode);
      }
    }
  }

  private class SaveAsAction implements Command {
    @Override
    public void execute() {
      ProjectRootNode projectRootNode = Ode.getInstance().getCurrentYoungAndroidProjectRootNode();
      if (projectRootNode != null) {
        ChainableCommand cmd = new SaveAllEditorsCommand(
            new CopyYoungAndroidProjectCommand(false));
        cmd.startExecuteChain(Tracking.PROJECT_ACTION_SAVE_AS_YA, projectRootNode);
      }
    }
  }

  private class CheckpointAction implements Command {
    @Override
    public void execute() {
      ProjectRootNode projectRootNode = Ode.getInstance().getCurrentYoungAndroidProjectRootNode();
      if (projectRootNode != null) {
        ChainableCommand cmd = new SaveAllEditorsCommand(
            new CopyYoungAndroidProjectCommand(true));
        cmd.startExecuteChain(Tracking.PROJECT_ACTION_CHECKPOINT_YA, projectRootNode);
      }
    }
  }

  private static class SwitchToProjectAction implements Command {
    @Override
    public void execute() {
      Ode.getInstance().switchToProjectsView();
    }
  }

  private class WirelessAction implements Command {
    @Override
    public void execute() {
      startRepl(true, false, false); // false means we are
      // *not* the emulator
    }
  }

  private class EmulatorAction implements Command {
    @Override
    public void execute() {
      startRepl(true, true, false); // true means we are the
      // emulator
    }
  }

  private class UsbAction implements Command {
    @Override
    public void execute() {
      startRepl(true, false, true);
    }
  }

  private class ResetAction implements Command {
    @Override
    public void execute() {
      startRepl(false, false, false); // We are really stopping the repl here
    }
  }

  private class BarcodeAction implements Command {
    @Override
    public void execute() {
      ProjectRootNode projectRootNode = Ode.getInstance().getCurrentYoungAndroidProjectRootNode();
      if (projectRootNode != null) {
        String target = YoungAndroidProjectNode.YOUNG_ANDROID_TARGET_ANDROID;
        ChainableCommand cmd = new SaveAllEditorsCommand(
            new GenerateYailCommand(
                new BuildCommand(target,
                    new ShowProgressBarCommand(target,
                        new WaitForBuildResultCommand(target,
                            new ShowBarcodeCommand(target)), "BarcodeAction"))));
//        updateBuildButton(true);
        cmd.startExecuteChain(Tracking.PROJECT_ACTION_BUILD_BARCODE_YA, projectRootNode,
            new Command() {
              @Override
              public void execute() {
//                updateBuildButton(false);
              }
            });
      }
    }
  }

  private class DownloadAction implements Command {
    @Override
    public void execute() {
      ProjectRootNode projectRootNode = Ode.getInstance().getCurrentYoungAndroidProjectRootNode();
      if (projectRootNode != null) {
        String target = YoungAndroidProjectNode.YOUNG_ANDROID_TARGET_ANDROID;
        ChainableCommand cmd = new SaveAllEditorsCommand(
            new GenerateYailCommand(
                new BuildCommand(target,
                    new ShowProgressBarCommand(target,
                        new WaitForBuildResultCommand(target,
                            new DownloadProjectOutputCommand(target)), "DownloadAction"))));
//        updateBuildButton(true);
        cmd.startExecuteChain(Tracking.PROJECT_ACTION_BUILD_DOWNLOAD_YA, projectRootNode,
            new Command() {
              @Override
              public void execute() {
//                updateBuildButton(false);
              }
            });
      }
    }
  }
  private static class ExportProjectAction implements Command {
    @Override
    public void execute() {
      List<Project> selectedProjects =
          ProjectListBox.getProjectListBox().getProjectList().getSelectedProjects();
      if (selectedProjects.size() == 1) {
        exportProject(selectedProjects.get(0));
      } else {
        // The user needs to select only one project.
        ErrorReporter.reportInfo(MESSAGES.wrongNumberProjectsSelected());
      }
    }

    private void exportProject(Project project) {
      Tracking.trackEvent(Tracking.PROJECT_EVENT,
          Tracking.PROJECT_ACTION_DOWNLOAD_PROJECT_SOURCE_YA, project.getProjectName());

      Downloader.getInstance().download(ServerLayout.DOWNLOAD_SERVLET_BASE +
          ServerLayout.DOWNLOAD_PROJECT_SOURCE + "/" + project.getProjectId());
    }
  }

  private static class ExportAllProjectsAction implements Command {
    @Override
    public void execute() {
      Tracking.trackEvent(Tracking.PROJECT_EVENT,
          Tracking.PROJECT_ACTION_DOWNLOAD_ALL_PROJECTS_SOURCE_YA);

      // Is there a way to disable the Download All button until this completes?
      if (Window.confirm(MESSAGES.downloadAllAlert())) {

        Downloader.getInstance().download(ServerLayout.DOWNLOAD_SERVLET_BASE +
            ServerLayout.DOWNLOAD_ALL_PROJECTS_SOURCE);
      }
    }
  }

  private static class ImportProjectAction implements Command {
    @Override
    public void execute() {
      new ProjectUploadWizard().center();
    }
  }

  private static class DeleteAction implements Command {
    @Override
    public void execute() {
      List<Project> selectedProjects =
          ProjectListBox.getProjectListBox().getProjectList().getSelectedProjects();
      if (selectedProjects.size() > 0) {
        // Show one confirmation window for selected projects.
        if (deleteConfirmation(selectedProjects)) {
          for (Project project : selectedProjects) {
            deleteProject(project);
          }
        }

      } else {
        // The user can select a project to resolve the
        // error.
        ErrorReporter.reportInfo(MESSAGES.noProjectSelectedForDelete());
      }
    }

    private boolean deleteConfirmation(List<Project> projects) {
      String message;
      if (projects.size() == 1) {
        message = MESSAGES.confirmDeleteSingleProject(projects.get(0).getProjectName());
      } else {
        StringBuilder sb = new StringBuilder();
        String separator = "";
        for (Project project : projects) {
          sb.append(separator).append(project.getProjectName());
          separator = ", ";
        }
        String projectNames = sb.toString();
        message = MESSAGES.confirmDeleteManyProjects(projectNames);
      }
      return Window.confirm(message);
    }

    private void deleteProject(Project project) {
      Tracking.trackEvent(Tracking.PROJECT_EVENT,
          Tracking.PROJECT_ACTION_DELETE_PROJECT_YA, project.getProjectName());

      final long projectId = project.getProjectId();

      Ode ode = Ode.getInstance();
      boolean isCurrentProject = (projectId == ode.getCurrentYoungAndroidProjectId());
      ode.getEditorManager().closeProjectEditor(projectId);
      if (isCurrentProject) {
        // If we're deleting the project that is currently open in the Designer we
        // need to clear the ViewerBox first.
        ViewerBox.getViewerBox().clear();
      }
      // Make sure that we delete projects even if they are not open.
      doDeleteProject(projectId);
    }

    private void doDeleteProject(final long projectId) {
      Ode.getInstance().getProjectService().deleteProject(projectId,
          new OdeAsyncCallback<Void>(
              // failure message
              MESSAGES.deleteProjectError()) {
            @Override
            public void onSuccess(Void result) {
              Ode.getInstance().getProjectManager().removeProject(projectId);
              // Show a welcome dialog in case there are no
              // projects saved.
              if (Ode.getInstance().getProjectManager().getProjects().size() == 0) {
                Ode.getInstance().createNoProjectsDialog(false);
              }
            }
          });
    }

  }

  private static class DownloadKeystoreAction implements Command {
    @Override
    public void execute() {
      Ode.getInstance().getUserInfoService().hasUserFile(StorageUtil.ANDROID_KEYSTORE_FILENAME,
          new OdeAsyncCallback<Boolean>(MESSAGES.downloadKeystoreError()) {
            @Override
            public void onSuccess(Boolean keystoreFileExists) {
              if (keystoreFileExists) {
                Tracking.trackEvent(Tracking.USER_EVENT, Tracking.USER_ACTION_DOWNLOAD_KEYSTORE);
                Downloader.getInstance().download(ServerLayout.DOWNLOAD_SERVLET_BASE +
                    ServerLayout.DOWNLOAD_USERFILE + "/" + StorageUtil.ANDROID_KEYSTORE_FILENAME);
              } else {
                Window.alert(MESSAGES.noKeystoreToDownload());
              }
            }
          });
    }
  }

  private class UploadKeystoreAction implements Command {
    @Override
    public void execute() {
      Ode.getInstance().getUserInfoService().hasUserFile(StorageUtil.ANDROID_KEYSTORE_FILENAME,
          new OdeAsyncCallback<Boolean>(MESSAGES.uploadKeystoreError()) {
            @Override
            public void onSuccess(Boolean keystoreFileExists) {
              if (!keystoreFileExists || Window.confirm(MESSAGES.confirmOverwriteKeystore())) {
                KeystoreUploadWizard wizard = new KeystoreUploadWizard(new Command() {
                  @Override
                  public void execute() {
                    Tracking.trackEvent(Tracking.USER_EVENT, Tracking.USER_ACTION_UPLOAD_KEYSTORE);
                    updateKeystoreFileMenuButtons();
                  }
                });
                wizard.center();
              }
            }
          });
    }
  }

  private class DeleteKeystoreAction implements Command {
    @Override
    public void execute() {
      final String errorMessage = MESSAGES.deleteKeystoreError();
      Ode.getInstance().getUserInfoService().hasUserFile(StorageUtil.ANDROID_KEYSTORE_FILENAME,
          new OdeAsyncCallback<Boolean>(errorMessage) {
            @Override
            public void onSuccess(Boolean keystoreFileExists) {
              if (keystoreFileExists && Window.confirm(MESSAGES.confirmDeleteKeystore())) {
                Tracking.trackEvent(Tracking.USER_EVENT, Tracking.USER_ACTION_DELETE_KEYSTORE);
                Ode.getInstance().getUserInfoService().deleteUserFile(
                    StorageUtil.ANDROID_KEYSTORE_FILENAME,
                    new OdeAsyncCallback<Void>(errorMessage) {
                      @Override
                      public void onSuccess(Void result) {
                        updateKeystoreFileMenuButtons();
                      }
                    });
              }
            }
          });
    }
  }

  /**
   * Enables and/or disables buttons based on how many projects exist
   * (in the case of "Download All Projects") or are selected (in the case
   * of "Delete" and "Download Source").
   */
  public void updateFileMenuButtons(boolean enablemenuitem) {
    fileDropDown.setItemEnabled(MESSAGES.deleteProjectButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.downloadAllButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.exportAllProjectsButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.exportProjectButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.importProjectButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.deleteKeystoreButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.downloadKeystoreButton(), enablemenuitem);
    fileDropDown.setItemEnabled(MESSAGES.uploadKeystoreButton(), enablemenuitem);
  }

  /**
   * Enables or disables buttons based on whether the user has an android.keystore file.
   */
  public void updateKeystoreFileMenuButtons() {
    Ode.getInstance().getUserInfoService().hasUserFile(StorageUtil.ANDROID_KEYSTORE_FILENAME,
        new AsyncCallback<Boolean>() {
          @Override
          public void onSuccess(Boolean keystoreFileExists) {
            fileDropDown.setItemEnabled(MESSAGES.deleteKeystoreButton(), keystoreFileExists);
            fileDropDown.setItemEnabled(MESSAGES.downloadKeystoreButton(), keystoreFileExists);
          }

          @Override
          public void onFailure(Throwable caught) {
            // Enable the buttons. If they are clicked, we'll check again if the keystore exists.
            fileDropDown.setItemEnabled(MESSAGES.deleteKeystoreButton(), true);
            fileDropDown.setItemEnabled(MESSAGES.downloadKeystoreButton(), true);
          }
        });
  }

  /**
   * Implements the action to generate the ".yail" file for each screen in the current project.
   * It does not build the entire project. The intention is that this will be helpful for
   * debugging during development, and will most likely be disabled in the production system.
   */
  private class GenerateYailAction implements Command {
    @Override
    public void execute() {
      ProjectRootNode projectRootNode = Ode.getInstance().getCurrentYoungAndroidProjectRootNode();
      if (projectRootNode != null) {
        String target = YoungAndroidProjectNode.YOUNG_ANDROID_TARGET_ANDROID;
        ChainableCommand cmd = new SaveAllEditorsCommand(new GenerateYailCommand(null));
        //updateBuildButton(true);
        cmd.startExecuteChain(Tracking.PROJECT_ACTION_BUILD_YAIL_YA, projectRootNode,
            new Command() {
              @Override
              public void execute() {
                //updateBuildButton(false);
              }
            });
      }
    }
  }

  private static class AboutAction implements Command {
    @Override
    public void execute() {
      final DialogBox db = new DialogBox(false, true);
      db.setText("About MIT App Inventor");
      db.setStyleName("ode-DialogBox");
      db.setHeight("200px");
      db.setWidth("400px");
      db.setGlassEnabled(true);
      db.setAnimationEnabled(true);
      db.center();

      VerticalPanel DialogBoxContents = new VerticalPanel();
      HTML message = new HTML(
          MESSAGES.gitBuildId(GitBuildId.getDate(), GitBuildId.getVersion()) +
              "<BR><BR>Please see " + RELEASE_NOTES_LINK_AND_TEXT +
              " and " + KNOWN_ISSUES_LINK_AND_TEXT  + "." +
              "<BR><BR>" + termsOfServiceText
      );

      SimplePanel holder = new SimplePanel();
      //holder.setStyleName("DialogBox-footer");
      Button ok = new Button("Close");
      ok.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
          db.hide();
        }
      });
      holder.add(ok);
      DialogBoxContents.add(message);
      DialogBoxContents.add(holder);
      db.setWidget(DialogBoxContents);
      db.show();

    }
  }

  private static class LibraryAction implements Command {
    @Override
    public void execute() {
      Window.open("http://dev-explore.appinventor.mit.edu/library", "_ai2", "");
    }
  }

  private static class GetStartedAction implements Command {
    @Override
    public void execute() {
      Window.open("http://dev-explore.appinventor.mit.edu/get-started", "_ai2", "");
    }
  }

  private static class TutorialsAction implements Command {
    @Override
    public void execute() {
      Window.open("http://dev-explore.appinventor.mit.edu/ai2/tutorials", "_ai2", "");
    }
  }

  private static class TroubleShootingAction implements Command {
    @Override
    public void execute() {
      Window.open("http://dev-explore.appinventor.mit.edu/ai2/support/troubleshooting", "_ai2",
          "");
    }
  }

  private static class ForumsAction implements Command {
    @Override
    public void execute() {
      Window.open("http://dev-explore.appinventor.mit.edu/forums", "_ai2", "");
    }
  }

  private static class FeedbackAction implements Command {
    @Override
    public void execute() {
      Window.open("http://something.example.com", "_blank", null);
    }
  }

  private void updateConnectToDropDownButton(boolean isEmulatorRunning, boolean isCompanionRunning, boolean isUsbRunning){
    if (!isEmulatorRunning && !isCompanionRunning && !isUsbRunning) {
      connectDropDown.setItemEnabled(MESSAGES.wirelessButton(), true);
      connectDropDown.setItemEnabled(MESSAGES.emulatorButton(), true);
      connectDropDown.setItemEnabled(MESSAGES.usbButton(), true);
      connectDropDown.setItemEnabled(MESSAGES.resetConnections(), false);
    } else {
      connectDropDown.setItemEnabled(MESSAGES.wirelessButton(), false);
      connectDropDown.setItemEnabled(MESSAGES.emulatorButton(), false);
      connectDropDown.setItemEnabled(MESSAGES.usbButton(), false);
      connectDropDown.setItemEnabled(MESSAGES.resetConnections(), true);
    }
  }

  /**
   * Indicate that we are no longer connected to the Companion, adjust
   * buttons accordingly. Called from BlocklyPanel
   */
  public static void indicateDisconnect() {
    TopToolbar instance = Ode.getInstance().getTopToolbar();
    instance.updateConnectToDropDownButton(false, false, false);
  }

  /**
   * startRepl -- Start/Stop the connection to the companion.
   *
   * @param start -- true to start the repl, false to stop it.
   * @param forEmulator -- true if we are connecting to the emulator.
   * @param forUsb -- true if this is a USB connection.
   *
   * If both forEmulator and forUsb are false, then we are connecting
   * via Wireless.
   */

  private void startRepl(boolean start, boolean forEmulator, boolean forUsb) {
    DesignToolbar.DesignProject currentProject = Ode.getInstance().getDesignToolbar().getCurrentProject();
    if (currentProject == null) {
      OdeLog.wlog("DesignToolbar.currentProject is null. "
            + "Ignoring attempt to start the repl.");
      return;
    }
    DesignToolbar.Screen screen = currentProject.screens.get(currentProject.currentScreen);
    screen.blocksEditor.startRepl(!start, forEmulator, forUsb);
    if (start) {
      if (forEmulator) {        // We are starting the emulator...
        updateConnectToDropDownButton(true, false, false);
      } else if (forUsb) {      // We are starting the usb connection
        updateConnectToDropDownButton(false, false, true);
      } else {                  // We are connecting via wifi to a Companion
        updateConnectToDropDownButton(false, true, false);
      }
    } else {
      updateConnectToDropDownButton(false, false, false);
    }
  }

}