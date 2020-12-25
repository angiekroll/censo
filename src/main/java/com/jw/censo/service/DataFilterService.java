package com.jw.censo.service;

import com.jw.censo.constants.AddressAbbreviation;
import com.jw.censo.constants.CardinalPoint;
import com.jw.censo.model.Person;
import com.jw.censo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataFilterService {

    private final PersonRepository personRepository;

    @Autowired
    public DataFilterService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> delegate(){
        List<Person> persons = getAllDataPerson();
        List<Person> personsCardinalPoint = validateCardinalPoint(persons);
        List<Person> personsFilter = validateAdress(personsCardinalPoint);
        return personsFilter;
    }

    public List<Person> getAllDataPerson() {
        return personRepository.findAll();
    }

    public List<Person> validateCardinalPoint(List<Person> persons) {
        List<Person> personsFilter = new ArrayList<>();
        persons.stream().forEach(person -> {
            if (person.getAdress().contains(CardinalPoint.CARDINAL_POINT_SUR.getName()) ||
                    person.getAdress().contains(CardinalPoint.CARDINAL_POINT_SUR.getName().toLowerCase())) {
                personsFilter.add(person);
            }
        });
        return personsFilter;
    }


    public List<Person> validateAdress(List<Person> persons){
        List<Person> personsFilter = new ArrayList<>();
         persons.forEach(person -> {
            if (person.getAdress().contains(AddressAbbreviation.CL) ||
                    person.getAdress().contains(AddressAbbreviation.CLL) ||
                    person.getAdress().contains(AddressAbbreviation.CALLE) ||
                    person.getAdress().contains(AddressAbbreviation.AC)) {

                String [] adress = person.getAdress().split(" ");
                System.out.println("ASI DIVIDE LA DEIRECCION EN UN ARRAY");
                System.out.println(adress);
                //TODO: condicion de rango para calles
                personsFilter.add(person);
            } else if (person.getAdress().contains(AddressAbbreviation.CR) ||
                    person.getAdress().contains(AddressAbbreviation.CRA) ||
                    person.getAdress().contains(AddressAbbreviation.KR) ||
                    person.getAdress().contains(AddressAbbreviation.CARRERA) ||
                    person.getAdress().contains(AddressAbbreviation.AK)) {
                //TODO: condicion de rango para carreras
                personsFilter.add(person);
            }else{
                personsFilter.add(person);
            }
        });
        return personsFilter;
    }

    //TODO: 1. OBTENER TODA LA DATA.
    // 2: FILTRAR POR SUR.
    // 3: ABREVIATURAS: CL, CLL, CALLE, AC
    //                  CR, CRA, KR, CARRERA, AK
    //                  DG, AV, TV  --> INVESTIGACION DENTRO DEL TERRITORIO EN MAPS
    // https://www.rankia.co/blog/dian/4269251-nomenclatura-dian-2020   --> Nomenclatura DIAN 2020 – Direcciones
    // Carrera	>= Kr 12 D	<= Kr 30
    // Calle >= Cll 22 Sur	<= Cll 31 Sur


}
