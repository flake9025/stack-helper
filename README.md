# Multi Tier Stack Builder Helper

- Master (1.0.0)

## Overview
This is the stack builder helper project, which allows easy implementation of CRUD REST Services.
Using generics types for required objects :
- Entity (Ex: Pet)
- Primary Key type (ex: Integer, Long, String, etc)
- DTO used for read operations (ex: findAll, etc)
- DTO used for create/update operations (ex: create, update) can be the same as the DTO used for read operations)

It will produce a Web Controller and a Service Layer, with the following operations :
- create
- create list
- count all
- find all
- find by ID
- update by ID
- update list
- delete all
- delete by ID
- delete list

### Prerequisites
- Spring MVC (for REST Controllers)
- Spring 5 (for IOC and Beans)
- Spring Data 2 (for Persistence)

## Getting Started

### Entities
If you already have Spring Data Persistable entities, this is already fine.
Otherwise, you can choose to add "implements Persistable<K>" where K is your primary key type.
Or, you can choose to extend any helper of this project :
	- AbstractModel class : generic entity
	- AbstractModelGeneratedId class  : generic entity with generated Id
	
for example :

```java
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class Pet extends AbstractModelGenereatedId<Integer>{
    private String name;
    private List<Pet> friends;
}
```

### DTO
All you have to do is to extend the Abstract Dto class.
You can choose to use the same DTO for both Read and Write operations, or use a different one for each.
for example :

Here we have a PetDTO with full data for readings :

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetDTO extends AbstractDto<Integer> {
	private String name;
	private List<PetDTO> friends;
}
```
And a PetWriteDTO with less data, for create and update operations :

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetWriteDTO extends AbstractDto<Integer> {
	private String name;
	private List<Long> friendsIds;
}
```

### Mappers
You will need mappers to walk from an Entity to a DTO, and vice versa.
Il you are familiar with MapStruct, all you have to do is to create an interface and extend the abstract Mapper class
for example :

```java
@Mapper(componentModel="spring")
public interface PetMapper extends AbstractMapper<Pet, Integer, PetDTO> {
	@Override
	public PetDTO mapToDto(Pet model);
}
```

If you don't want to use MapStruct, you can still write your own mapper class :

```java
public class PetMapperImpl extends AbstractMapper<Pet, Integer, PetDTO> {

	@Autowired
	private PetDao petDao;
	
	@Override
	public PetDTO mapToDto(Pet model){
		PetDTO dto = new PetDTO();
		dto.setName(model.getName());
		List<PetDTO> friends = new ArrayList<PetDTO>());
		for(Pet pet : model.getFriends()){
			friends.add(mapToDto(pet));
		}
		dto.setFriends(friends);
		return dto;
	}
```

### Service Layer
All you have to do is to extend the abstract Service class, and fill the "updateModel" method with business logic.
for example :

```java
@Service
public class PetService extends AbstractService<Pet, Integer, PetDTO, PetWriteDTO> {

	@Autowired
	private PetDao petDao;
	
	@Override
	protected Pet updateModel(final Pet model, final PetWriteDTO dto) {
		// check name
		if(dto.getName().isEmpty()) {
			// pet name should be unique !
			if(petDao.findByName(dto.getName()) == null){
				model.setName(dto.getName());
			}
		}
		// check friends
		for(Integer friendId : dto.getFriendsIds()){
			// pet friend should exists !
			if(petDao.existsById(friendId)){
				model.getFriends().add(petDao.findById(friendId));
			}
		}
		return model;
	}
}
```

### Controller layer
All you have to do is to extend the abstract Controller class.
for example :

```java
@RestController
@RequestMapping("/pets")
@Api
public class PetController extends AbstractRestController<Pet, Integer, PetDTO, PetWriteDTO> {
	// crud endpoints are already defined !
	// add only new services
}
```
