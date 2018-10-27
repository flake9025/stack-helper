package fr.vvlabs.stackhelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import fr.vvlabs.stackhelper.dto.AbstractDto;
import fr.vvlabs.stackhelper.mapper.AbstractMapper;

/**
 * @author Vincent Villain Abstract Service Layer for CRUD operations
 *
 * @param <T> the model type
 * @param <K> the primary key type
 * @param <S> the Read DTO type
 * @param <U> the Create/Update DTO type
 */
public abstract class AbstractService<T extends Persistable<K>, K extends Serializable, S extends AbstractDto<K>, U extends AbstractDto<K>> {

	// ===========================================================
	// Fields
	// ===========================================================

	@Autowired
	private CrudRepository<T, K> dao;

	@Autowired
	private AbstractMapper<T, K, S, U> mapper;

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	/**
	 * Update model.
	 *
	 * @param model the model
	 * @param dto the dto
	 */
	protected abstract T updateModel(final T model, final U dto);
	
	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Count all.
	 *
	 * @return the count
	 */
	@Transactional(readOnly = true)
	public long countAll() {
		return dao.count();
	}

	/**
	 * Find all.
	 *
	 * @return the read dto list
	 */
	@Transactional(readOnly = true)
	public List<S> findAll() {
		return StreamSupport.stream(dao.findAll().spliterator(), false).map(mapper::mapToDto).collect(Collectors.toList());
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the read dto
	 */
	@Transactional(readOnly = true)
	public S findById(K id) {
		S dto = null;
		Optional<T> model = dao.findById(id);
		if (model.isPresent()) {
			dto = mapper.mapToDto(model.get());
		}
		return dto;
	}

	/**
	 * Create.
	 *
	 * @param dto the create dto
	 * @return the created model
	 */
	@Transactional
	public T create(final U dto) {
		return dao.save(mapper.mapToModel(dto));
	}
	
	/**
	 * Update.
	 *
	 * @param dto the update dto
	 * @return the updated model
	 */
	@Transactional
	public T update(final K id, final U dto) {
		Optional<T> model = dao.findById(id);
		if (model.isPresent()) {
			T updatedModel = updateModel(model.get(), dto);
			return dao.save(updatedModel);
		}
		return null;
	}

	/**
	 * Save all.
	 *
	 * @param dtoList the create/update dto list
	 * @return the created/updated models
	 */
	public List<T> saveAll(final List<U> dtoList) {
		Iterable<T> models = dao.saveAll(dtoList.stream().map(mapper::mapToModel).collect(Collectors.toList()));
		return StreamSupport.stream(models.spliterator(), false).collect(Collectors.toList());
	}

	/**
	 * Delete.
	 *
	 * @param model the model
	 */
	@Transactional
	public void delete(final T model) {
		dao.delete(model);
	}

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	@Transactional
	public void deleteById(final K id) {
		dao.deleteById(id);
	}

	/**
	 * Delete by id list.
	 *
	 * @param idList the id list
	 */
	@Transactional
	public void deleteByIdList(final List<K> idList) {
		idList.forEach(dao::deleteById);
	}
}
