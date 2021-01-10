package com.skocz.mateusz.zilch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClientDTO implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
}
