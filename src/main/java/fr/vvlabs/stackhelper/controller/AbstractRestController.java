package fr.vvlabs.stackhelper.controller;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	 * @return the object count
	 */
	@GetMapping(value = "/count")
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
	 * @return the object list
	 */
	@GetMapping(params = { "page", "size" })
	public ResponseEntity<List<S>> findAll( //
			@RequestParam(value = "page", defaultValue = "0") int page, //
			@RequestParam(value = "size",  defaultValue = "30") int size, //
			@RequestParam(value = "sort",  required = false) String sort //
			) { //
		try {
			return ResponseEntity.ok(service.findAll(page, size, sort));
		} catch (Exception e) {
			log.error("findAll() KO : {}", e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the object
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<S> findById(@PathVariable K id) {
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
	 * Create.
	 *
	 * @param dto the dto
	 * @return the key
	 */
	@PostMapping
	public ResponseEntity<K> create(@RequestBody U dto) {
		try {
			K newKey = service.create(dto);

			if (newKey == null)
				return ResponseEntity.noContent().build();

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newKey)
					.toUri();

			return ResponseEntity.created(location).build();
		} catch (Exception e) {
			log.error("create({}) KO : {}", dto, e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Create list.
	 *
	 * @param dto the dto
	 * @return the key list
	 */
	@PostMapping(value = "/createAll")
	public ResponseEntity<List<K>> createAll(@RequestBody List<U> dtoList) {
		try {
			List<K> newKeyList = service.createAll(dtoList);

			if (CollectionUtils.isEmpty(newKeyList))
				return ResponseEntity.noContent().build();

			return ResponseEntity.ok(newKeyList);
		} catch (Exception e) {
			log.error("createAll({}) KO : {}", dtoList, e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Update.
	 *
	 * @param dto the dto
	 * @return the key
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<K> update(@PathVariable K id, @RequestBody U dto) {
		try {
			K savedKey = service.update(id, dto);

			if (savedKey == null)
				return ResponseEntity.noContent().build();

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedKey)
					.toUri();

			return ResponseEntity.created(location).build();
		} catch (Exception e) {
			log.error("save({}) KO : {}", dto, e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Update list.
	 *
	 * @param dto the dto
	 * @return the key list
	 */
	@PutMapping(value = "/updateAll")
	public ResponseEntity<List<K>> updateAll(@RequestBody List<U> dtoList) {
		try {
			List<K> keyList = service.updateAll(dtoList);

			if (CollectionUtils.isEmpty(keyList))
				return ResponseEntity.noContent().build();

			return ResponseEntity.ok(keyList);
		} catch (Exception e) {
			log.error("updateAll({}) KO : {}", dtoList, e.getMessage(), e);
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 * @return the response entity
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable K id) {
		try {
			service.deleteById(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("delete({}) KO : {}", id, e.getMessage(), e);
			return ResponseEntity.noContent().build();
		}
	}

	/**
	 * Delete list.
	 *
	 * @param idList the id list
	 * @return the response entity
	 */
	@DeleteMapping(value = "/deleteAll")
	public ResponseEntity<Void> deleteAll(@RequestBody List<K> idList) {
		try {
			service.deleteByIdList(idList);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("deleteList({}) KO : {}", idList, e.getMessage(), e);
			return ResponseEntity.noContent().build();
		}
	}
}
