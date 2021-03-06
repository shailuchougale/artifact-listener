package fr.openwide.maven.artifact.notifier.web.application.artifact.component;

import java.util.regex.Pattern;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import fr.openwide.core.wicket.behavior.ClassAttributeAppender;
import fr.openwide.core.wicket.markup.html.panel.GenericPanel;
import fr.openwide.maven.artifact.notifier.web.application.common.component.LabelWithPlaceholder;

public class ArtifactVersionTagPanel extends GenericPanel<String> {

	private static final long serialVersionUID = -8022735891213157213L;
	
	private static final Pattern NON_FINAL_VERSION_PATTERN = Pattern.compile(".*[\\.-](rc|cr|beta|alpha|incubating).*", Pattern.CASE_INSENSITIVE);

	private static final String CSS_FINAL_VERSION_TAG = "label label-info";
	
	private static final String CSS_NON_FINAL_VERSION_TAG = "label label-warning";
	
	public ArtifactVersionTagPanel(String id, IModel<? extends String> model) {
		this(id, model, true);
	}
	
	public ArtifactVersionTagPanel(String id, IModel<? extends String> model, boolean showPlaceholder) {
		super(id, model);
		IModel<String> cssClassModel = new LoadableDetachableModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String load() {
				String version = getModelObject();
				if (version != null && NON_FINAL_VERSION_PATTERN.matcher(version).matches()) {
					return CSS_NON_FINAL_VERSION_TAG;
				}
				return CSS_FINAL_VERSION_TAG;
			}
		};
		
		LabelWithPlaceholder latestVersionLabel = new LabelWithPlaceholder("latestVersion", model);
		latestVersionLabel.setHideIfEmpty(!showPlaceholder);
		latestVersionLabel.add(new ClassAttributeAppender(cssClassModel));
		add(latestVersionLabel);
	}
}
