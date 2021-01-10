package com.skocz.mateusz.zilch.model;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public List<ClientDTO> clientListToClientDtoList(List<Client> clients) {

        return clients.stream()
                .map(this::clientToClientDto)
                .collect(Collectors.toList());
    }

    public ClientDTO clientToClientDto(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .build();
    }

    public Client clientDtoToClient(ClientDTO clientDTO) {
        return Client.builder()
                .id(clientDTO.getId())
                .firstName(clientDTO.getFirstName())
                .lastName(clientDTO.getLastName())
                .build();
    }

}
