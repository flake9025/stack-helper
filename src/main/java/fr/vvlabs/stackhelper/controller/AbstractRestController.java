package fr.vvlabs.stackhelper.controller;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.vvlabs.stackhelper.dto.AbstractDto;
import fr.vvlabs.stackhelper.service.AbstractService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Vincent Villain Abstract Rest Controller Layer for CRUD operations
 *
 * @param <T> the model type
 * @param <K> the primary key type
 * @param <S> the Read DTO type
 * @param <U> the Create/Update DTO type
 */
@Slf4j
public abstract class AbstractRestController<T extends Persistable<K>, K extends Serializable, S extends AbstractDto<K>, U extends AbstractDto<K>> {

	// ===========================================================
	// Fields
	// ===========================================================

	@Autowired
	private AbstractService<T, K, S, U> service;

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Count all.
	 *
	 * @return the response
	 */
	@GetMapping(value="/count")
	public ResponseEntity<Long> countAll() {
		try {
			return ResponseEntity.ok(service.countAll());
		} catch (Exception e) {
			log.error("countAll() KO : {}", e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Find all.
	 *
	 * @return the response
	 */
	@GetMapping
	public ResponseEntity<List<S>> findAll() {
		try {
			return ResponseEntity.ok(service.findAll());
		} catch (Exception e) {
			log.error("findAll() KO : {}", e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the response
	 */
	@GetMapping(value="/{id}")
	public ResponseEntity<S> findById(K id) {
		try {
			S dto = service.findById(id);
			if (dto != null) {
				return ResponseEntity.ok(dto);
			} else {
				throw new NotFoundException();
			}
		} catch (Exception e) {
			log.error("findById({}) KO : {}", id, e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Save.
	 *
	 * @param dto the dto
	 * @return the response
	 */
    @PostMapping
	public ResponseEntity<T> save(U dto) {
		try {
			T savedObject = service.save(dto);
			
	        if (savedObject == null)
	            return ResponseEntity.noContent().build();
	        
	        URI location = ServletUriComponentsBuilder
	                .fromCurrentRequest()
	                .path("/{id}")
	                .buildAndExpand(savedObject.getId())
	                .toUri();
	        
	        return ResponseEntity.created(location).build();
		} catch (Exception e) {
			log.error("save({}) KO : {}", dto, e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}
}
