package com.jw.censo.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PersonFilter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    @Column(unique=true)
    private Integer phone;
    private String state;
    private LocalDateTime date;

    public PersonFilter() {
    }
}
