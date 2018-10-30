package fr.vvlabs.stackhelper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.vvlabs.stackhelper.dao.PetDao;
import fr.vvlabs.stackhelper.dto.PetDTO;
import fr.vvlabs.stackhelper.dto.PetWriteDTO;
import fr.vvlabs.stackhelper.model.Pet;

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
				model.getFriends().add(petDao.findById(friendId).get());
			}
		}
		return model;
	}
}
