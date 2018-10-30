package fr.vvlabs.stackhelper.model;

import java.util.List;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
