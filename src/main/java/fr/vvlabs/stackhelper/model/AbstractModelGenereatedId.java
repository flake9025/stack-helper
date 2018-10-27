package fr.vvlabs.stackhelper.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The abstract Class AbstractModelGenereatedId.
 *
 * @param <K> the primary key type
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public abstract class AbstractModelGenereatedId<K extends Serializable> implements Persistable<K> {

	// ===========================================================
	// Fields
	// ===========================================================

    @Id
    @GeneratedValue
	protected K id;
    
    @Transient
    private Boolean isNew;
	
    // ===========================================================
 	// Constructors
 	// ===========================================================
 	
    protected AbstractModelGenereatedId() {
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
		return this.isNew();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public void setId(final K id) {
        this.id = id;
        this.isNew = false;
    }
}
