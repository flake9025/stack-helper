package fr.vvlabs.stackhelper.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The abstract Class AbstractModel.
 *
 * @param <K> the primary key type
 */
@MappedSuperclass
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@ToString
public abstract class AbstractModel<K extends Serializable> implements Persistable<K> {

	// ===========================================================
	// Fields
	// ===========================================================

    @Id
	protected K id;
    
    @Transient
    private Boolean isNew;
	
    // ===========================================================
 	// Constructors
 	// ===========================================================
 	
    protected AbstractModel() {
    	super();
        this.isNew = true;
    }
    
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public K getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return this.isNew;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public void setId(final K id) {
        this.id = id;
        this.isNew = false;
    }
}
