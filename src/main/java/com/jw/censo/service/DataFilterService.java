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
                    person.getAdress().contains(AddressAbbreviation.AC.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.DG) ||
                    person.getAdress().contains(AddressAbbreviation.DG.toLowerCase())) {
                String addressFormat = person.getAdress().replaceAll(CardinalPoint.CARDINAL_POINT_SUR.getName(), ""); // formato a la dirección eliminando caracteres especiales y palabra "sur"
                addressFormat = addressFormat.replaceAll("sur", "");
                addressFormat = addressFormat.replaceAll("BIS", "");
                addressFormat = addressFormat.replaceAll("bis", "");
                addressFormat = addressFormat.replaceAll("#", "");
                String[] adressArray = addressFormat.split(" "); // convierte la direccion en un array de string
                String[] adressArrayFormat = Arrays.stream(adressArray).filter(chart -> !chart.isEmpty()).toArray(String[]::new); // genera nuevo array eliminando posiciones vacias
                if (adressArrayFormat[1].length() <= 3 && adressArrayFormat[2].length() <= 3) {  // valida que no vengan mas de tres caracteres en el numero de la calle
                    String mainRoadNumber = validateMainRoadNumber(adressArrayFormat);
                    if (adressArrayFormat[1].length() < 3 || Integer.parseInt(mainRoadNumber) != 31){  /// para validar las calles 31 sin ninguna letra
                        String roadGeneratorNumber = validateRoadGeneratorNumber(adressArrayFormat);
                        if (Integer.parseInt(mainRoadNumber) >= 22 && Integer.parseInt(mainRoadNumber) <= 31) {  // valida el rango del territorio para calles
                            if (Integer.parseInt(roadGeneratorNumber) >= 12 && Integer.parseInt(roadGeneratorNumber) <= 30) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                                char letra = adressArrayFormat[2].charAt(2);
                                if(letra >= 'D') {  //validacion roadGeneratorNumber (carrera) 12D
                                    personsFilter.add(person);
                                }
                            }
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
                    person.getAdress().contains(AddressAbbreviation.AK.toLowerCase()) ||
                    person.getAdress().contains(AddressAbbreviation.TV) ||
                    person.getAdress().contains(AddressAbbreviation.TV.toLowerCase())) {
                // TODO: agregar agregar validacion de 12D para carreras
                // formato a la dirección eliminando caracteres especiales y palabra "sur"
                String addressFormat = person.getAdress().replaceAll(CardinalPoint.CARDINAL_POINT_SUR.getName(), "");
                addressFormat = addressFormat.replaceAll("sur", "");
                addressFormat = addressFormat.replaceAll("BIS", "");
                addressFormat = addressFormat.replaceAll("bis", "");
                addressFormat = addressFormat.replaceAll("#", "");
                String[] adressArray = addressFormat.split(" "); // convierte la direccion en un array de string
                String[] adressArrayFormat = Arrays.stream(adressArray).filter(chart -> !chart.isEmpty()).toArray(String[]::new); // genera nuevo array eliminando posiciones vacias
                if (adressArrayFormat[1].length() <= 3 && adressArrayFormat[2].length() <= 3) {  // valida que no vengan mas de tres caracteres en el numero de la calle
                    String mainRoadNumber = validateMainRoadNumber(adressArrayFormat);
                    String roadGeneratorNumber = validateRoadGeneratorNumber(adressArrayFormat);
                    if (Integer.parseInt(mainRoadNumber) >= 14 && Integer.parseInt(mainRoadNumber) <= 30) {  // valida el rango del territorio para calles
                        if (Integer.parseInt(roadGeneratorNumber) >= 22 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                            personsFilter.add(person);
                        }
                    } else if (Integer.parseInt(mainRoadNumber) >= 12 && Integer.parseInt(mainRoadNumber) <= 30) {
                        char letra = adressArrayFormat[1].charAt(2);
                        if(letra >= 'D'){  //validacion roadmain (carrera) 12D
                            if (Integer.parseInt(roadGeneratorNumber) >= 27 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                                personsFilter.add(person);
                            }
                        }
                    }
                }
            } else {
                //   personsFilter.add(person);
            }
        });
        return personsFilter;
    }


    public boolean isnumeric(char character) {
        try {
            Integer.parseInt(String.valueOf(character));
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }


    public String validateMainRoadNumber(String[] adressArrayFormat) {
        String mainRoadNumber;
        if (adressArrayFormat[1].length() == 1) {
            mainRoadNumber = adressArrayFormat[1].substring(0, 1);
        } else {
            char charTwo = adressArrayFormat[1].charAt(1);
            if (isnumeric(charTwo)) {
                mainRoadNumber = adressArrayFormat[1].substring(0, 2); // extrae solo los dos numeros y quita las letras en caso de que vengan
            } else {
                mainRoadNumber = adressArrayFormat[1].substring(0, 1);
            }

        }
        return mainRoadNumber;
    }


    public String validateRoadGeneratorNumber(String[] adressArrayFormat) {
        String roadGeneratorNumber;
        if (adressArrayFormat[2].length() == 1) {
            roadGeneratorNumber = adressArrayFormat[2].substring(0, 1);
        } else {
            char charTwo = adressArrayFormat[2].charAt(1);
            if (isnumeric(charTwo)) {
                roadGeneratorNumber = adressArrayFormat[2].substring(0, 2); // extrae solo los dos numeros y quita las letras en caso de que vengan
            } else {
                roadGeneratorNumber = adressArrayFormat[2].substring(0, 1);
            }
        }
        return roadGeneratorNumber;
    }


    //TODO PROCESAMIENTO:
    // 1. OBTENER TODA LA DATA.
    // 2: FILTRAR POR CARDINALIDAD "SUR" Y ELIMINAR LOS "ESTE".
    // 3: ABREVIATURAS: CL, CLL, CALLE, AC, DG  --> CALLES
    //                  CR, CRA, KR, CARRERA, AK, TV   ---> CARRERAS
    //                  AV  --> INVESTIGACION DENTRO DEL TERRITORIO EN MAPS
    // https://www.rankia.co/blog/dian/4269251-nomenclatura-dian-2020   --> Nomenclatura DIAN 2020 – Direcciones
    // CONDICIONES DE RANGOS TERRITORIO OLAYA
    // Carrera	>= Kr 12 D	<= Kr 30
    // Calle >= Cll 22 Sur	<= Cll 31 Sur


    //TODO PRECONDICIONES DEL ARCHIVO:
    // Se debe unificar la letra que acompañe a la via principal o via generadora.  --> ejemplo: TV 18I BIS A 72 36 SUR
    // Se deben quitar todos los "BIS A", despues de las vias principales para evitar una nueva posicion en el array.  --> ejemplo: cl 27A sur # 14B - 44
    // Formateo a lenguaje SQL


    //TODO PENDIENTE:
    // Guardar el resultado en DB con un estado de migrado.
    // Cambiad DB no volatil
    // Cambiar por address
    // reorganizar código
    // validar quitar el indicativo


}
