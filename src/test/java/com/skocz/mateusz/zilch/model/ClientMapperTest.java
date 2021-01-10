package com.skocz.mateusz.zilch.model;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private ClientMapper mapper = new ClientMapper();

    @Test
    public void shouldMapClientToClientDTO() {
        // Given
        final Client client = Client.builder()
                .id(1L)
                .firstName("F")
                .lastName("L")
                .build();

        // When
        final ClientDTO dto = mapper.clientToClientDto(client);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(client.getId());
        assertThat(dto.getFirstName()).isEqualTo(client.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(client.getLastName());
    }

    @Test
    public void shouldMapClientDtoToClient() {
        // Given
        final ClientDTO dto = ClientDTO.builder()
                .id(1L)
                .firstName("F")
                .lastName("L")
                .build();

        // When
        final Client client = mapper.clientDtoToClient(dto);

        // Then
        assertThat(client).isNotNull();
        assertThat(client.getId()).isEqualTo(dto.getId());
        assertThat(client.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(client.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    public void shouldMapClientListToToClientDto() {
        // Given
        final Client client1 = Client.builder()
                .id(1L)
                .firstName("F")
                .lastName("L")
                .build();

        final Client client2 = Client.builder()
                .id(2L)
                .firstName("F2")
                .lastName("L2")
                .build();

        // When
        final List<ClientDTO> clientDTOList = mapper.clientListToClientDtoList(Lists.list(client1, client2));

        // Then
        assertThat(clientDTOList)
                .isNotNull()
                .hasSize(2);

        final ClientDTO dto1 = clientDTOList.get(0);

        assertThat(dto1).isNotNull();
        assertThat(dto1.getId()).isEqualTo(client1.getId());
        assertThat(dto1.getFirstName()).isEqualTo(client1.getFirstName());
        assertThat(dto1.getLastName()).isEqualTo(client1.getLastName());

        final ClientDTO dto2 = clientDTOList.get(1);

        assertThat(dto1).isNotNull();
        assertThat(dto2.getId()).isEqualTo(client2.getId());
        assertThat(dto2.getFirstName()).isEqualTo(client2.getFirstName());
        assertThat(dto2.getLastName()).isEqualTo(client2.getLastName());
    }

}