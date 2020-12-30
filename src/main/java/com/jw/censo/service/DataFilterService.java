package com.jw.censo.service;

import com.jw.censo.constants.CardinalPoint;
import com.jw.censo.constants.RoadNomenclature;
import com.jw.censo.model.Person;
import com.jw.censo.model.PersonFilter;
import com.jw.censo.repository.PersonFilterRepository;
import com.jw.censo.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DataFilterService {

    private final PersonRepository personRepository;
    private final PersonFilterRepository personFilterRepository;

    @Autowired
    public DataFilterService(PersonRepository personRepository, PersonFilterRepository personFilterRepository) {
        this.personRepository = personRepository;
        this.personFilterRepository = personFilterRepository;
    }

    public List<Person> PersonFilterDelegate() {
        List<Person> personsFilter = new ArrayList<>();
        List<Person> persons = getAllPerson();
        List<Person> personsCardinalPoint = validateCardinalPoint(persons);
        personsCardinalPoint.forEach(personCardinalPoint -> {
            String[] addressArrayFormat = formatAddress(personCardinalPoint.getAdress());
            String roadNomenclature = validateRoadNomenclature(addressArrayFormat, personCardinalPoint);
            if (validateResidentialNomenclature(roadNomenclature, addressArrayFormat)) {
                personsFilter.add(personCardinalPoint);
                PersonFilter personFilter = buildPersonFilter(personCardinalPoint);
                savePersonFilter(personFilter);
            }
        });
        return personsFilter;
    }


    public List<Person> getAllPerson() {
        return personRepository.findAll();
    }


    public void savePersonFilter(PersonFilter personFilter) {
        try {
            personFilterRepository.save(personFilter);
        } catch (Throwable ex) {
            log.info("[---------------------> phone: {}] Error when trying to insert the phone number, duplicate number.",
                    personFilter.getPhone());
        }
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


    public String validateRoadNomenclature(String[] addressArrayFormat, Person person) {
        String roadNomenclature = null;
        if (addressArrayFormat[1].length() <= 3 && addressArrayFormat[2].length() <= 3) {  // valida que no vengan mas de tres caracteres en el numero de la calle o carrera
            if (person.getAdress().contains(RoadNomenclature.CL) || person.getAdress().contains(RoadNomenclature.CL.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.CLL) || person.getAdress().contains(RoadNomenclature.CLL.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.CALLE) || person.getAdress().contains(RoadNomenclature.CALLE.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.AC) || person.getAdress().contains(RoadNomenclature.AC.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.DG) || person.getAdress().contains(RoadNomenclature.DG.toLowerCase())) {
                roadNomenclature = RoadNomenclature.CALLE;
            } else if (person.getAdress().contains(RoadNomenclature.CR) || person.getAdress().contains(RoadNomenclature.CR.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.CRA) || person.getAdress().contains(RoadNomenclature.CRA.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.KR) || person.getAdress().contains(RoadNomenclature.KR.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.CARRERA) || person.getAdress().contains(RoadNomenclature.CARRERA.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.AK) || person.getAdress().contains(RoadNomenclature.AK.toLowerCase()) ||
                    person.getAdress().contains(RoadNomenclature.TV) || person.getAdress().contains(RoadNomenclature.TV.toLowerCase())) {
                roadNomenclature = RoadNomenclature.CARRERA;
            } else {
                // TODO: colocar exception INDICANDO QUE LA NOMENCLATURA VIAL NO EXISTE DENTRO E LAS PARAMETRIZADAS.
            }
        }
        return roadNomenclature;
    }


    public boolean isnumeric(char character) {
        try {
            Integer.parseInt(String.valueOf(character));
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }


    public String getMainRoadNumber(String[] adressArrayFormat) {
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


    public String getRoadGeneratorNumber(String[] adressArrayFormat) {
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


    public String[] formatAddress(String address) {
        // formato a la dirección eliminando caracteres especiales y palabra "sur"
        String addressFormat = address.replaceAll(CardinalPoint.CARDINAL_POINT_SUR.getName(), "");
        addressFormat = addressFormat.replaceAll("sur", "");
        addressFormat = addressFormat.replaceAll("BIS", "");
        addressFormat = addressFormat.replaceAll("bis", "");
        addressFormat = addressFormat.replaceAll("#", "");
        String[] adressArray = addressFormat.split(" "); // convierte la direccion en un array de string
        String[] adressArrayFormat = Arrays.stream(adressArray).filter(chart -> !chart.isEmpty()).toArray(String[]::new); // genera nuevo array eliminando posiciones vacias
        return adressArrayFormat;
    }


    public boolean validateResidentialNomenclature(String roadNomenclature, String[] addressArrayFormat) {
        String mainRoadNumber = getMainRoadNumber(addressArrayFormat);
        String roadGeneratorNumber = getRoadGeneratorNumber(addressArrayFormat);
        switch (roadNomenclature) {
            case RoadNomenclature.CALLE:
                if (addressArrayFormat[1].length() < 3 || Integer.parseInt(mainRoadNumber) != 31) {  /// para validar las calles 31 sin ninguna letra
                    if (Integer.parseInt(mainRoadNumber) >= 22 && Integer.parseInt(mainRoadNumber) <= 31) {  // valida el rango del territorio para calles
                        if (Integer.parseInt(mainRoadNumber) >= 27 && Integer.parseInt(roadGeneratorNumber) >= 12 && Integer.parseInt(roadGeneratorNumber) <= 30) { // valida el rango del territorio para carrera correspondiente a la calle anterior y teniendo encuenta que la carrera 12 solo aplica para la calle 27 en adelante
                            if (Integer.parseInt(roadGeneratorNumber) == 12 && addressArrayFormat[2].length() == 3) {
                                char letra = addressArrayFormat[2].charAt(2);
                                if (letra >= 'D') {  //validacion roadGeneratorNumber (carrera) 12D
                                    return true;
                                }
                            } else if (Integer.parseInt(roadGeneratorNumber) != 12 &&
                                    (addressArrayFormat[2].length() < 3 ||
                                            Integer.parseInt(roadGeneratorNumber) != 30)) { // valida que no pase la carrera 12 sola y adicional que la carrera 30 no venga con letras.
                                return true;
                            } else {
                                return false;
                            }
                        } else if (Integer.parseInt(roadGeneratorNumber) >= 14 && Integer.parseInt(roadGeneratorNumber) <= 30) {
                            if (addressArrayFormat[2].length() < 3 || Integer.parseInt(roadGeneratorNumber) != 30) { // valida que no pase la carrera 12 sola y adicional que la carrera 30 no venga con letras.
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
                break;

            case RoadNomenclature.CARRERA:
                if (Integer.parseInt(mainRoadNumber) >= 12 && Integer.parseInt(mainRoadNumber) <= 30) {
                    if (Integer.parseInt(mainRoadNumber) == 12 && addressArrayFormat[1].length() == 3) {
                        char letra = addressArrayFormat[1].charAt(2);
                        if (letra >= 'D') {  //validacion roadmain (carrera) 12D
                            if (addressArrayFormat[2].length() < 3 || Integer.parseInt(roadGeneratorNumber) != 31) {
                                if (Integer.parseInt(roadGeneratorNumber) >= 27 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                                    return true;
                                }
                            }
                        }
                    } else if (Integer.parseInt(mainRoadNumber) == 13) {
                        if (addressArrayFormat[2].length() < 3 || Integer.parseInt(roadGeneratorNumber) != 31) {
                            if (Integer.parseInt(roadGeneratorNumber) >= 27 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                                return true;
                            }
                        }

                    } else if (Integer.parseInt(mainRoadNumber) >= 14 && Integer.parseInt(mainRoadNumber) <= 30) {
                        // valida el rango del territorio para calles
                        if (addressArrayFormat[2].length() < 3 || Integer.parseInt(roadGeneratorNumber) != 31) {
                            if (Integer.parseInt(roadGeneratorNumber) >= 22 && Integer.parseInt(roadGeneratorNumber) <= 31) { // valida el rango del territorio para carrera correspondiente a la calle anterior
                                return true;
                            }
                        }
                    }
                } else {
                    return false;
                }
                break;
            default:
                // TODO: COLOCAR UNA EXCEPCIÓN QUE INDIQUE NO EXISTE LA NOMENCLATURA VIAL PARAMETRIZADA EN EL SISTEMA.
                throw new IllegalStateException("Unexpected value: " + roadNomenclature);
        }
        return false;
    }

    public PersonFilter buildPersonFilter(Person personCardinalPoint) {
        PersonFilter personFilter = new PersonFilter();
        personFilter.setAddress(personCardinalPoint.getAdress());
        personFilter.setPhone(personCardinalPoint.getPhone());
        personFilter.setState("N");
        personFilter.setDate(LocalDateTime.now());
        return personFilter;
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
    // Cambiad DB no volatil
    // Cambiar por address
    // reorganizar código
    // validar quitar el indicativo

    //Todo: VENTAJAS:
    // 1. Evita la duplicidad de datos
    // 2. la validacion de direcciones se hace automaticamente. (se evita el estar preguntando o validando direcciones
    // sobretodo hermanos de avanzada edad)
    // 3. Reduce el tiempo del censo.
    // 4. Se tienen los registros en una BD gestionada para mayor facilidad de busquedas.



}
