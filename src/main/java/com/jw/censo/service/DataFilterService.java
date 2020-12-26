package com.jw.censo.service;

import com.jw.censo.constants.AddressAbbreviation;
import com.jw.censo.constants.CardinalPoint;
import com.jw.censo.model.Person;
import com.jw.censo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DataFilterService {

    private final PersonRepository personRepository;

    @Autowired
    public DataFilterService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> delegate() {
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
            if (!person.getAdress().contains(CardinalPoint.CARDINAL_POINT_ESTE.getName()) &&
                    !person.getAdress().contains(CardinalPoint.CARDINAL_POINT_ESTE.getName().toLowerCase())) {
                if (person.getAdress().contains(CardinalPoint.CARDINAL_POINT_SUR.getName()) ||
                        person.getAdress().contains(CardinalPoint.CARDINAL_POINT_SUR.getName().toLowerCase())) {
                    personsFilter.add(person);
                }

            }
        });
        return personsFilter;
    }


    public List<Person> validateAdress(List<Person> persons) {
        List<Person> personsFilter = new ArrayList<>();
        persons.forEach(person -> {
            if (person.getAdress().contains(AddressAbbreviation.CL) ||
                    person.getAdress().contains(AddressAbbreviation.CL.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.CLL) ||
                    person.getAdress().contains(AddressAbbreviation.CLL.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.CALLE) ||
                    person.getAdress().contains(AddressAbbreviation.CALLE.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.AC) ||
                    person.getAdress().contains(AddressAbbreviation.AC.toLowerCase())) {
                // TODO: agregar validacion si contiene 31 y una letra lo elimina esto solo para calles, y agregar validacion de 12D para carreras
                // formato a la dirección eliminando caracteres especiales y palabra "sur"
                String addressFormat = person.getAdress().replaceAll(CardinalPoint.CARDINAL_POINT_SUR.getName(), "");
                addressFormat = addressFormat.replaceAll("sur", "");
                addressFormat = addressFormat.replaceAll("BIS", "");
                addressFormat = addressFormat.replaceAll("bis", "");
                addressFormat = addressFormat.replaceAll("#", "");
                String[] adressArray = addressFormat.split(" "); // convierte la direccion en un array de string
                String[] adressArrayFormat = Arrays.stream(adressArray).filter(chart -> !chart.isEmpty()).toArray(String[]::new); // genera nuevo array eliminando posiciones vacias
                if (adressArrayFormat[1].length() <= 3) {  // valida que no vengan mas de tres caracteres en el numero de la calle
                    String mainRoadNumber;
                    String roadGeneratorNumber;
                    if (adressArrayFormat[1].length() == 1) {
                        mainRoadNumber = adressArrayFormat[1].substring(0, 1);
                    } else {
                        mainRoadNumber = adressArrayFormat[1].substring(0, 2); // extrae solo los dos numeros y quita las letras en caso de que vengan
                    }
                    if (Integer.parseInt(mainRoadNumber) >= 22 && Integer.parseInt(mainRoadNumber) <= 31) {  // valida el rango del territorio para calles
                        roadGeneratorNumber = adressArrayFormat[2].substring(0, 2);
                        if (Integer.parseInt(roadGeneratorNumber) >= 12 && Integer.parseInt(roadGeneratorNumber) <= 30) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                            personsFilter.add(person);
                        }
                    }
                }
            } else if (person.getAdress().contains(AddressAbbreviation.CR) ||
                    person.getAdress().contains(AddressAbbreviation.CR.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.CRA) ||
                    person.getAdress().contains(AddressAbbreviation.CRA.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.KR) ||
                    person.getAdress().contains(AddressAbbreviation.KR.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.CARRERA) ||
                    person.getAdress().contains(AddressAbbreviation.CARRERA.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.AK) ||
                    person.getAdress().contains(AddressAbbreviation.AK.toLowerCase())) {
                // formato a la dirección eliminando caracteres especiales y palabra "sur"
                String addressFormat = person.getAdress().replaceAll(CardinalPoint.CARDINAL_POINT_SUR.getName(), "");
                addressFormat = addressFormat.replaceAll("sur", "");
                addressFormat = addressFormat.replaceAll("BIS", "");
                addressFormat = addressFormat.replaceAll("bis", "");
                addressFormat = addressFormat.replaceAll("#", "");
                String[] adressArray = addressFormat.split(" "); // convierte la direccion en un array de string
                String[] adressArrayFormat = Arrays.stream(adressArray).filter(chart -> !chart.isEmpty()).toArray(String[]::new); // genera nuevo array eliminando posiciones vacias
                if (adressArrayFormat[1].length() <= 3) {  // valida que no vengan mas de tres caracteres en el numero de la calle
                    String mainRoadNumber;
                    String roadGeneratorNumber;
                    if (adressArrayFormat[1].length() == 1) {
                        mainRoadNumber = adressArrayFormat[1].substring(0, 1);
                    } else {
                        //TODO: Validar aqui por que puede llegar un "4G" y va a fallar mas adelante. validar que sea numerico mejor.
                        mainRoadNumber = adressArrayFormat[1].substring(0, 2); // extrae solo los dos numeros y quita las letras en caso de que vengan
                    }
                    if (Integer.parseInt(mainRoadNumber) >= 14 && Integer.parseInt(mainRoadNumber) <= 30) {  // valida el rango del territorio para calles
                        roadGeneratorNumber = adressArrayFormat[2].substring(0, 2);
                        if (Integer.parseInt(roadGeneratorNumber) >= 22 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                            personsFilter.add(person);
                        }
                    } else if (Integer.parseInt(mainRoadNumber) >= 12 && Integer.parseInt(mainRoadNumber) <= 30) {
                        roadGeneratorNumber = adressArrayFormat[2].substring(0, 2);
                        if (Integer.parseInt(roadGeneratorNumber) >= 27 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                            personsFilter.add(person);
                        }
                    }
                }
            } else {
                //   personsFilter.add(person);
            }
        });
        return personsFilter;
    }

    public boolean isnumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    //TODO: 1. OBTENER TODA LA DATA.
    // 2: FILTRAR POR CARDINALIDAD "SUR" Y ELIMINAR LOS "ESTE".
    // 3: ABREVIATURAS: CL, CLL, CALLE, AC  --> CALLES
    //                  CR, CRA, KR, CARRERA, AK   ---> CARRERAS
    //                  DG, AV, TV  --> INVESTIGACION DENTRO DEL TERRITORIO EN MAPS
    // https://www.rankia.co/blog/dian/4269251-nomenclatura-dian-2020   --> Nomenclatura DIAN 2020 – Direcciones
    // CONDICIONES DE RANGOS TERRITORIO OLAYA
    // Carrera	>= Kr 12 D	<= Kr 30
    // Calle >= Cll 22 Sur	<= Cll 31 Sur


}
