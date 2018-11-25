package fr.vvlabs.stackhelper.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Vincent Villain Abstract Persistable Model.
 *
 * @param <K> the primary key type
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public abstract class AbstractModel<K extends Serializable> implements Persistable<K> {

	// ===========================================================
	// Fields
	// ===========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	protected K id;
   
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public K getId() {
		return id;
	}

	@Override
	@Transient // DATAJPA-622
	public boolean isNew() {
		return null == getId();
	}
}
