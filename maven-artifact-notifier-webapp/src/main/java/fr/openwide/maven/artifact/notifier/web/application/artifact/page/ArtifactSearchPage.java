package fr.openwide.maven.artifact.notifier.web.application.artifact.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;
import fr.openwide.core.wicket.more.markup.html.template.model.BreadCrumbElement;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.AdvisableArtifactPortfolioPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.component.ArtifactSearchResultsPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.form.ArtifactSearchPanel;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.ArtifactBeanDataProvider;
import fr.openwide.maven.artifact.notifier.web.application.artifact.model.AdvisableArtifactDataProvider;
import fr.openwide.maven.artifact.notifier.web.application.common.template.MainTemplate;

@AuthorizeInstantiation(CoreAuthorityConstants.ROLE_AUTHENTICATED)
public class ArtifactSearchPage extends MainTemplate {

	private static final long serialVersionUID = 2780987980751053482L;

	public ArtifactSearchPage(PageParameters parameters) {
		super(parameters);

		addBreadCrumbElement(new BreadCrumbElement(new ResourceModel("artifact.follow.search.pageTitle"), getPageClass()));
		add(new Label("pageTitle", new ResourceModel("artifact.follow.search.pageTitle")));
		
		IModel<String> globalSearchModel = new Model<String>();
		IModel<String> searchGroupModel = new Model<String>();
		IModel<String> searchArtifactModel = new Model<String>();
		
		add(new AdvisableArtifactPortfolioPanel("advisableArtifacts",
				new AdvisableArtifactDataProvider(globalSearchModel, searchGroupModel, searchArtifactModel), Integer.MAX_VALUE));
		
		ArtifactSearchResultsPanel artifactSearchResultsPanel = new ArtifactSearchResultsPanel("artifactSearchResultsPanel",
				new ArtifactBeanDataProvider(globalSearchModel, searchGroupModel, searchArtifactModel));
		add(artifactSearchResultsPanel);
		
		add(new ArtifactSearchPanel("artifactSearchPanel", artifactSearchResultsPanel.getDataView(), globalSearchModel,
				searchGroupModel, searchArtifactModel));
	}

	@Override
	protected Class<? extends WebPage> getFirstMenuPage() {
		return ArtifactSearchPage.class;
	}
	
	@Override
	protected Class<? extends WebPage> getSecondMenuPage() {
		return ArtifactSearchPage.class;
	}
}
