package com.jw.censo.controller;

import com.jw.censo.model.Person;
import com.jw.censo.service.DataFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class DataFilterController {

    private DataFilterService dataFilterService;

    @Autowired
    public DataFilterController(DataFilterService dataFilterService) {
        this.dataFilterService = dataFilterService;
    }

    @GetMapping("test")
    public ResponseEntity<List<Person>> getLocationShip(){
        return new ResponseEntity<>(dataFilterService.delegate(), HttpStatus.OK);
    }


}
