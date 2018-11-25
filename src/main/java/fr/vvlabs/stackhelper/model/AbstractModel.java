package fr.vvlabs.stackhelper.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Vincent Villain Abstract Persistable Model.
 *
 * @param <K> the primary key type
 */
@MappedSuperclass
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class AbstractModel<K extends Serializable> extends AbstractPersistable<K> {
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	/* (non-Javadoc)
	 * @see org.springframework.data.jpa.domain.AbstractPersistable#setId(java.io.Serializable)
	 * Override this method to get a public setId, otherwise create and update will not work !
	 */
	@Override
	public void setId(final K id) {
		super.setId(id);
    }
}
