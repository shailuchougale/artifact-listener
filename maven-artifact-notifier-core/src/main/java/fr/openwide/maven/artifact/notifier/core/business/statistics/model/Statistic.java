package fr.openwide.maven.artifact.notifier.core.business.statistics.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.bindgen.Bindable;
import org.hibernate.search.annotations.DocumentId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.openwide.core.jpa.business.generic.model.GenericEntity;

@Bindable
@Entity
public class Statistic extends GenericEntity<Long, Statistic> {

	private static final long serialVersionUID = -4172333476805159348L;

	@Id
	@GeneratedValue
	@DocumentId
	private Long id;
	
	@Column(unique = true, nullable = false)
	@Enumerated(EnumType.STRING)
	private StatisticEnumKey enumKey;
	
	@Column(nullable = false)
	private Integer value;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public StatisticEnumKey getEnumKey() {
		return enumKey;
	}
	
	public void setEnumKey(StatisticEnumKey enumKey) {
		this.enumKey = enumKey;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getNameForToString() {
		return getEnumKey().name();
	}

	@Override
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	public String getDisplayName() {
		return getNameForToString();
	}
	
	public static enum StatisticEnumKey {
		NOTIFICATIONS_SENT_PER_DAY,
		VERSIONS_RELEASED_PER_DAY
	}
}
