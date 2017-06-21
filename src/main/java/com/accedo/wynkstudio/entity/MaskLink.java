package com.accedo.wynkstudio.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Accedo Software Private Limited
 * @version 1.0
 * @since 2014-07-01
 *        <p>
 *        The persistent class for the user_recent database table.
 */
/**
 * @author Suresh
 *
 */
@Entity
@Table(name = "user_masklink", uniqueConstraints=@UniqueConstraint(columnNames={"hashCode", "url"}))
public class MaskLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String hashCode;
	
	@Column(nullable = false)
	private String url;

	@SuppressWarnings("unused")
	private Timestamp created;

	@PrePersist
	protected void onCreate() {
		created = new Timestamp(new java.util.Date().getTime());
	}
	
	public MaskLink() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

	
}