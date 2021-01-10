package com.skocz.mateusz.zilch.model;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
}
