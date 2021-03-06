package fr.openwide.maven.artifact.notifier.web.application.administration.component;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.maven.artifact.notifier.core.business.user.model.User;
import fr.openwide.maven.artifact.notifier.core.business.user.model.UserGroup;
import fr.openwide.maven.artifact.notifier.core.business.user.service.IUserGroupService;
import fr.openwide.maven.artifact.notifier.core.util.binding.Binding;
import fr.openwide.maven.artifact.notifier.web.application.administration.page.AdministrationUserDescriptionPage;
import fr.openwide.maven.artifact.notifier.web.application.common.component.UserAutocompleteAjaxComponent;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.core.wicket.more.markup.html.feedback.FeedbackUtils;
import fr.openwide.core.wicket.more.markup.html.template.js.jquery.plugins.bootstrap.confirm.component.AjaxConfirmLink;
import fr.openwide.core.wicket.more.model.BindingModel;
import fr.openwide.core.wicket.more.model.GenericEntityModel;

public class UserGroupMembersPanel extends GenericPanel<UserGroup> {

	private static final long serialVersionUID = 1955579250974258074L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupMembersPanel.class);

	@SpringBean
	private IUserGroupService userGroupService;

	private ListView<User> memberListView;

	public UserGroupMembersPanel(String id, IModel<UserGroup> userGroupModel) {
		super(id, userGroupModel);
		
		// Members list
		memberListView = new ListView<User>("members", BindingModel.of(getModel(), Binding.userGroup().persons())) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(final ListItem<User> item) {
				BookmarkablePageLink<User> userLink = new BookmarkablePageLink<User>(
						"userLink", 
						AdministrationUserDescriptionPage.class, 
						LinkUtils.getUserPageParameters(item.getModelObject())
				);
				userLink.add(new Label("userName", BindingModel.of(item.getModel(), Binding.user().userName())));
				item.add(userLink);
				
				item.add(new Label("fullName", BindingModel.of(item.getModel(), Binding.user().fullName())));
				
				IModel<String> confirmationTextModel = new StringResourceModel(
						"administration.usergroup.members.delete.confirmation.text",
						null, new Object[] {
								item.getModelObject().getDisplayName(),
								UserGroupMembersPanel.this.getModelObject().getName()
						}
				);
				
				item.add(new AjaxConfirmLink<User>("deleteLink", item.getModel(),
						new ResourceModel("administration.usergroup.members.delete.confirmation.title"),
						confirmationTextModel,
						new ResourceModel("common.confirm"),
						new ResourceModel("common.cancel"),
						null, false) {
					private static final long serialVersionUID = -5179621361619239269L;
					
					@Override
					public void onClick(AjaxRequestTarget target) {
						try {
							UserGroup userGroup = UserGroupMembersPanel.this.getModelObject();
							User user = getModelObject();
							
							userGroupService.removePerson(userGroup, user);
							Session.get().success(getString("administration.usergroup.members.delete.success"));
						} catch (Exception e) {
							LOGGER.error("Error occured while removing user from user group", e);
							Session.get().error(getString("administration.usergroup.members.delete.error"));
						}
						target.add(getPage());
						FeedbackUtils.refreshFeedback(target, getPage());
					}
				});
			}
		};
		add(memberListView);
		
		add(new WebMarkupContainer("emptyList") {
			private static final long serialVersionUID = 6700720373087584498L;

			@Override
			public void onConfigure() {
				super.onConfigure();
				setVisible(memberListView.size() <= 0);
			}
		});
		
		// Add member form
		IModel<User> emptyUserModel = new GenericEntityModel<Long, User>(null);
		
		final UserAutocompleteAjaxComponent userAutocomplete = new UserAutocompleteAjaxComponent("userAutocomplete",
				emptyUserModel);
		userAutocomplete.setAutoUpdate(true);
		
		final Form<User> addMemberForm = new Form<User>("addMemberForm", emptyUserModel);
		addMemberForm.add(userAutocomplete);
		addMemberForm.add(new AjaxSubmitLink("addMemberLink", addMemberForm) {
			private static final long serialVersionUID = 6935376642872117563L;
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				UserGroup userGroup = UserGroupMembersPanel.this.getModelObject();
				User selectedUser = userAutocomplete.getModelObject();
				
				if (selectedUser != null) {
					if (!userGroup.getPersons().contains(selectedUser)) {
						try {
							userGroupService.addPerson(userGroup, selectedUser);
							getSession().success(getString("administration.usergroup.members.add.success"));
						} catch (Exception e) {
							LOGGER.error("Unknown error occured while adding a user to a usergroup", e);
							getSession().error(getString("administration.usergroup.members.add.error"));
						}
					} else {
						LOGGER.error("User already added to this group");
						getSession().warn(getString("administration.usergroup.members.add.alreadyMember"));
					}
				}
				userAutocomplete.setModelObject(null);
				target.add(getPage());
				FeedbackUtils.refreshFeedback(target, getPage());
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				FeedbackUtils.refreshFeedback(target, getPage());
			}
		});
		add(addMemberForm);
	}
}
