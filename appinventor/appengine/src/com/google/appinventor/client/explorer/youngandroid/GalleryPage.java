// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client.explorer.youngandroid;

import com.google.appinventor.client.Ode;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.explorer.project.Project;
import com.google.appinventor.client.explorer.project.ProjectComparators;
import com.google.appinventor.client.explorer.project.ProjectManagerEventListener;

import com.google.appinventor.client.output.OdeLog;
import com.google.appinventor.shared.rpc.project.GalleryApp;
import com.google.appinventor.shared.rpc.project.GalleryComment;
import com.google.appinventor.client.GalleryClient;
import com.google.appinventor.client.GalleryRequestListener;
import com.google.appinventor.client.OdeAsyncCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.user.client.ui.Image;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import com.google.appinventor.client.wizards.NewProjectWizard.NewProjectCommand;
import com.google.appinventor.shared.rpc.project.UserProject;

import com.google.appinventor.shared.rpc.project.youngandroid.NewYoungAndroidProjectParameters;
import com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidProjectNode;
import com.google.gwt.user.client.Window;

/**
 * The gallery list shows apps from the gallery in a table.
 *
 * <p> The project name and date created will be shown in the table.
 *
 * @author wolberd@google.com (Dave Wolber)
 */
public class GalleryPage extends Composite implements GalleryRequestListener {
  

  GalleryClient gallery = new GalleryClient(this);
  GalleryApp app = null;

  private final FlowPanel galleryGUI;
  private final FlowPanel appSingle;
  private final FlowPanel appSummary;
  private final FlowPanel appDetails;
  private final FlowPanel appHeader;
  private final FlowPanel appAction;
  private final FlowPanel appMeta;
  private final FlowPanel appDates;
  private final FlowPanel appDescription;
  private final FlowPanel appComments;
  private final FlowPanel appCommentsList;
  
  /**
   * Creates a new GalleryPage
   */
  public GalleryPage(final GalleryApp app) {

    this.app = app;
    // Initialize UI
    VerticalPanel panel = new VerticalPanel();
    panel.setWidth("100%");
    
    galleryGUI = new FlowPanel();
    appSingle = new FlowPanel();
    appSummary = new FlowPanel();
    appDetails = new FlowPanel();
    appHeader = new FlowPanel();
    appAction = new FlowPanel();
    appMeta = new FlowPanel();
    appDates = new FlowPanel();
    appDescription = new FlowPanel();
    appComments = new FlowPanel();
    appCommentsList = new FlowPanel();

    // App summary - header image
    appSummary.add(appHeader);
    appHeader.addStyleName("app-header");
    Image image = new Image();
    image.setUrl(app.getImageURL());
    image.addStyleName("app-image");
    appHeader.add(image);
    
    // App summary - action
    appSummary.add(appAction);
    Button actionButton = new Button("Try this app");
    actionButton.addClickHandler(new ClickHandler() {
      // Open up source file if clicked the action button
      public void onClick(ClickEvent event) {
        OdeLog.log("######## I clicked on actionButton - ");
        gallery.loadSourceFile(app.getProjectName(),app.getSourceURL());
      }
    });
    actionButton.addStyleName("app-action");
    appAction.add(actionButton);

    // App details - header title
    Label title = new Label(app.getTitle());
    appDetails.add(title);
    title.addStyleName("app-title");
    
    Label devName = new Label("By " + app.getDeveloperName());
    appDetails.add(devName);
    devName.addStyleName("app-subtitle");
    
    // App details - meta
    appDetails.add(appMeta);
    appMeta.addStyleName("app-meta");
    
    // Images for meta data
    Image numViews = new Image();
    numViews.setUrl("http://i.imgur.com/jyTeyCJ.png");
    Image numDownloads = new Image();
    numDownloads.setUrl("http://i.imgur.com/j6IPJX0.png");
    Image numLikes = new Image();
    numLikes.setUrl("http://i.imgur.com/N6Lpeo2.png");
    Image numComments = new Image();
    numComments.setUrl("http://i.imgur.com/GGt7H4c.png");
    
    // Add meta data
    appMeta.add(numViews);
    appMeta.add(new Label(Integer.toString(app.getViews())));
    appMeta.add(numDownloads);
    appMeta.add(new Label(Integer.toString(app.getDownloads())));
    appMeta.add(numLikes);
    appMeta.add(new Label(Integer.toString(app.getLikes())));
    appMeta.add(numComments);
    appMeta.add(new Label(Integer.toString(app.getComments())));
    
    // Add app dates
    appDetails.add(appDates);
    Date creationDate = new Date(Long.parseLong(app.getCreationDate()));
    Date updateDate = new Date(Long.parseLong(app.getUpdateDate()));
    DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy/MM/dd hh:mm:ss a");
    Label creation = new Label("Created on " + dateFormat.format(creationDate));
    Label update = new Label("Updated on " + dateFormat.format(creationDate));
    appDates.add(creation);
    appDates.add(update);
    appDates.addStyleName("app-dates");

    // App details - description
    appDetails.add(appDescription);
    Label description = new Label(app.getDescription());
    appDescription.add(description);
    appDescription.addStyleName("app-description");
    
    HTML divider = new HTML("<div class='section-divider'></div>");
    appDetails.add(divider);
    
    // App details - comments
    appDetails.add(appComments);
    Label commentsHeader = new Label("Comments");
    commentsHeader.addStyleName("app-comments-header");
    appComments.add(commentsHeader);

    // Add list of comments
    gallery.GetComments(app.getGalleryAppId(), 0, 100);
    appComments.add(appCommentsList);
    appCommentsList.addStyleName("app-comments");
    

    appSingle.add(appSummary);
    appSummary.addStyleName("gallery-app-summary");
    appSingle.add(appDetails);
    appDetails.addStyleName("gallery-app-details");
    galleryGUI.add(appSingle);
    appSingle.addStyleName("gallery-app-single");
    panel.add(galleryGUI);
    galleryGUI.addStyleName("gallery");
    initWidget(panel);
  }

@Override
public void onAppListRequestCompleted(List<GalleryApp> apps, int requestID) {
	// TODO Auto-generated method stub
	
}

@Override
public void onCommentsRequestCompleted(List<GalleryComment> comments) {
	// TODO Auto-generated method stub
    if (comments != null) {
    	for ( GalleryComment c : comments) {
    	  FlowPanel commentItem = new FlowPanel();
        FlowPanel commentPerson = new FlowPanel();
        FlowPanel commentMeta = new FlowPanel();
        FlowPanel commentContent = new FlowPanel();
        
        // Add commentPerson, default avatar for now
        Image cPerson = new Image();
        cPerson.setUrl("http://i.imgur.com/1h7cUkM.png");
    	  commentPerson.add(cPerson);
    	  commentPerson.addStyleName("comment-person");
    	  commentItem.add(commentPerson);
    	  
    	  // Add commentContent
    		Label cAuthor = new Label(c.getAuthor());
    		cAuthor.addStyleName("comment-author");
        commentMeta.add(cAuthor);

        Date commentDate = new Date(Long.parseLong(c.getTimeStamp()));
        DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy/MM/dd hh:mm:ss a");
        Label cDate = new Label(" on " + dateFormat.format(commentDate));
        cDate.addStyleName("comment-date");
    		commentMeta.add(cDate);

        commentMeta.addStyleName("comment-meta");
        commentContent.add(commentMeta);

        Label cText = new Label(c.getText());
        cText.addStyleName("comment-text");
        commentContent.add(cText);

        commentContent.addStyleName("comment-content");
        commentItem.add(commentContent);
        
    		commentItem.addStyleName("comment-item");
    		appCommentsList.add(commentItem);
    	}
    } 
    else {
        Window.alert("comment list was null");    	
    }
}

@Override
public void onSourceLoadCompleted(UserProject projectInfo) {
    final NewProjectCommand onSuccessCommand = new NewProjectCommand() {
        @Override
        public void execute(Project project) {
             Ode.getInstance().openYoungAndroidProjectInDesigner(project);
        }
     };
     // Update project explorer -- i.e., display in project view
     final Ode ode = Ode.getInstance();
     if (projectInfo == null) {
       Window.alert("Unable to create project from Gallery source"); 
     }
     else {
       Project project = ode.getProjectManager().addProject(projectInfo);
       if (onSuccessCommand != null) {
         onSuccessCommand.execute(project);
       }
     }
	
}
 
}	  
  
  

 
