# Multi Tier Stack Builder Helper

- Master (1.0.0)

## Overview
This is the stack builder helper project, which allows easy implementation of CRUD REST Services.
Using generics types for required objects :
- Entity
- Primary Key
- DTO used for read operations
- DTO used for create operations (can be the same)
it will produce a Web Controller and a Service Layer, with the following operations :
- create
- countAll
- findAll
- findById
- update
- deleteById
- deleteByIdList

### Prerequisites
- Spring 5
- Spring Data 2

## Getting Started

### Models
If you already have Spring Data Persistable entities, this is already fine.
Otherwise, you can choose to add "implements Persistable<K>" where K is your primary key type.
Or, you can choose to use any helper of this project :
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
}
```

### DTO
All you have to do is to extend the AbstracDto class.
You can choose to use the same DTO for both Read and Write operations, or use a different one for each.
for example :

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
@ToString
public class PetDTO extends AbstractDto<Integer> {

	private static final long serialVersionUID = -6010374309568122948L;

	private String name;
}
```

### Service Layer
All you have to do is to extend the Service class, and fill the "updateModel" method with business logic.
for example :

```java
@Service
public class PetService extends AbstractService<Pet, Integer, PetDTO, PetDTO> {
	@Override
	protected Pet updateModel(final Pet model, final PetDTO dto) {
		// some business logic :
		if(!dto.getName().isEmpty() {
			model.setName(dto.getName());
		}
		return model;
	}
}
```

### Controller layer
All you have to do is to extend the Controller class.
for example :

```java
@RestController
@RequestMapping("/pets")
@Api
public class PetController extends AbstractRestController<Pet, Integer, PetDTO, PetDTO>{
}
```