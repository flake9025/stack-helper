package fr.vvlabs.stackhelper.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.Predicate;

import fr.vvlabs.stackhelper.dao.AbstractDAO;
import fr.vvlabs.stackhelper.dto.AbstractDto;
import fr.vvlabs.stackhelper.mapper.AbstractMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Vincent Villain Abstract Service Layer for CRUD operations
 *
 * @param <T> the model type
 * @param <K> the primary key type
 * @param <S> the Read DTO type
 * @param <U> the Create/Update DTO type
 */
@Slf4j
public abstract class AbstractService<T extends Persistable<K>, K extends Serializable, S extends AbstractDto<K>, U extends AbstractDto<K>> {

	// ===========================================================
	// Fields
	// ===========================================================

	@Autowired
	private AbstractDAO<T, K> dao;

	@Autowired
	private AbstractMapper<T, K, S> mapper;
	
    /**
     * Model class name
     */
    private Class<T> modelType;

	// ===========================================================
	// Constructors
	// ===========================================================
	
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbstractService() {
        Class obtainedClass = getClass();
        Type genericSuperclass = null;
        while (obtainedClass != null) {
            genericSuperclass = obtainedClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                break;
            }
            obtainedClass = obtainedClass.getSuperclass();
        }
        ParameterizedType genericType = (ParameterizedType) genericSuperclass;
        if(genericType != null) {
        	this.modelType = (Class<T>) genericType.getActualTypeArguments()[0];
        }
    }

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	/**
	 * Update model.
	 *
	 * @param model the model
	 * @param dto the dto
	 */
	protected abstract void updateModel(T model, U dto);
	
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
	 * Find all with pagination and sorting.
	 *
	 * @param searchDtoList the search dto list
	 * @param page the page
	 * @param size the size
	 * @param sort the sort
	 * @return the read dto list
	 */
	@Transactional(readOnly = true)
	public Page<S> findAll(Predicate predicate, int page, int size, String sort) {
		// build search criterias
		// choose between custom Specifications, RSQL and QueryDSL !
		//Specification<T> specifications = buildSpecifications(searchDtoList)
		// build sort conditions
		Direction sortDirection = sort != null && sort.startsWith("-") ? Direction.DESC : Direction.ASC;
		String[] sortFields = sort != null ? sort.split(",") : null;
		// build pageable request
		Pageable pageableRequest = sortFields != null ? PageRequest.of(page, size, sortDirection, sortFields) : PageRequest.of(page, size);
		// find all , then map results
		return dao.findAll(predicate, pageableRequest).map(mapper::mapToDto);
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
		} else {
			log.error("findById({}) : object not found", id); 
		}
		return dto;
	}
	
	/**
	 * Exists by id.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	@Transactional(readOnly = true)
	public boolean existsById(K id) {
		return dao.existsById(id);
	}

	/**
	 * Create.
	 *
	 * @param dto the create dto
	 * @return the created model key
	 */
	@Transactional
	public K create(final U dto) {
		K key = null;
		T model = dao.save(mapToModel(dto));
		if(model != null) {
			key = model.getId();
		} else {
			log.error("create() : object not saved : {}", dto); 
		}
		return key;
	}
	
	/**
	 * Create all.
	 *
	 * @param dtoList the create dto list
	 * @return the created model keys
	 */
	public List<K> createAll(final List<U> dtoList) {
		List<K> keyList = null;
		Iterable<T> modelIterable = dao.saveAll(dtoList.stream().map(this::mapToModel).collect(Collectors.toList()));
		if(modelIterable != null) {
			List<T> modelList = StreamSupport.stream(modelIterable.spliterator(), false).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(modelList)) {
				keyList = modelList.stream().map(T::getId).collect(Collectors.toList());
			}
		} else {
			log.error("createAll() : objects not saved {}", dtoList); 
		}
		return keyList;
	}
	
	/**
	 * Update.
	 *
	 * @param dto the update dto
	 * @return the updated key
	 */
	@Transactional
	public K update(final K id, final U dto) {
		K key = null;
		Optional<T> optionalModel = dao.findById(id);
		if (optionalModel.isPresent()) {
			T model = optionalModel.get();
			updateModel(model, dto);
			key = dao.save(model).getId();
		} else {
			log.error("update({}) : object not found", id); 
		}
		return key;
	}

	/**
	 * Update all.
	 *
	 * @param dtoList the create/update dto list
	 * @return the created/updated models
	 */
	public List<K> updateAll(final List<U> dtoList) {
		List<K> keyList = null;
		// update models from dto
		List<T> updatedModelList = new ArrayList<>();
		for(U dto : dtoList) {
			Optional<T> optionalModel = dao.findById(dto.getId());
			if (optionalModel.isPresent()) {
				T model = optionalModel.get();
				updateModel(model, dto);
				updatedModelList.add(model);
			} else {
				log.error("updateAll({}) : object not found", dto.getId()); 
			}
		}
		// save new models
		Iterable<T> modelIterable = dao.saveAll(updatedModelList);
		if(modelIterable != null) {
			List<T> modelList = StreamSupport.stream(modelIterable.spliterator(), false).collect(Collectors.toList());
			if(!CollectionUtils.isEmpty(modelList)) {
				keyList = modelList.stream().map(T::getId).collect(Collectors.toList());
			}
		}
		return keyList;
	}

	/**
	 * Delete all.
	 */
	@Transactional
	public void deleteAll() {
		dao.deleteAll();
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
	
	/**
	 * Delete all.
	 *
	 * @param modelList the model list
	 */
	@Transactional
	public void deleteAll(List<T> modelList) {
		dao.deleteAll(modelList);
	}
	
    
    /**
     * Convert Create DTO to Model
     * 
     * @param dto
     * @return model
     */
    private T mapToModel(U dto) {
        if (dto == null)
            return null;

        T model = null;
        try {
            model = modelType.newInstance();
            updateModel(model, dto);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("mapToModel(...) KO: " + e, e);
        }
        return model;
    }
}
