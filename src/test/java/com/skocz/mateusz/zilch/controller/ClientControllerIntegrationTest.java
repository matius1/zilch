package com.skocz.mateusz.zilch.controller;

import com.skocz.mateusz.zilch.ZilchApplication;
import com.skocz.mateusz.zilch.model.ClientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        classes = ZilchApplication.class,
        webEnvironment = RANDOM_PORT)
public class ClientControllerIntegrationTest {

    public static final String LOCALHOST = "http://localhost:";
    public static final String CLIENT = "/client";
    public static final String UPDATE = CLIENT + "/update";
    public static final String PURGE = CLIENT + "/purge";
    public static final String FIND_ALL = CLIENT + "/all";
    public static final String BY_ID = CLIENT + "/id/";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ClientDTO testClient = ClientDTO.builder()
            .firstName("F Name")
            .lastName("L Name")
            .build();

    @BeforeEach
    public void clearDb() {
        ResponseEntity<String> purgeResponse = restTemplate.getForEntity(LOCALHOST + port + PURGE, String.class);
        assertThat(purgeResponse.getBody()).isEqualTo("DB and Cache cleared");

        ResponseEntity<List> findAllResposne = restTemplate.getForEntity(LOCALHOST + port + FIND_ALL, List.class);
        assertThat(findAllResposne.getBody()).isEmpty();
    }

    @Test
    public void shouldGetEmptyListWhenNoClients() {
        // When
        ResponseEntity<List> responseEntity = restTemplate.getForEntity(LOCALHOST + port + FIND_ALL, List.class);

        //Then
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    public void shouldAddOneClient() {
        // Given
        HttpEntity<ClientDTO> request = new HttpEntity<>(testClient);

        // When
        ResponseEntity<ClientDTO> addClientEntity = restTemplate.postForEntity(LOCALHOST + port + UPDATE, request, ClientDTO.class);

        // Then
        ClientDTO newClient = addClientEntity.getBody();

        assertThat(newClient.getId()).isNotNull();
        assertThat(newClient.getFirstName()).isEqualTo(testClient.getFirstName());
        assertThat(newClient.getLastName()).isEqualTo(testClient.getLastName());
    }

    @Test
    public void shouldGetOneClient() {
        // Given
        addClient();

        // When
        ResponseEntity<ClientDTO[]> responseEntity = restTemplate.getForEntity(LOCALHOST + port + FIND_ALL, ClientDTO[].class);

        // Then
        List<ClientDTO> storedClient = getClientDTOs(responseEntity);
        assertThat(storedClient).hasSize(1);

        ClientDTO client = storedClient.get(0);
        assertThat(client.getId()).isNotNull();
        assertThat(client.getFirstName()).isEqualTo(testClient.getFirstName());
        assertThat(client.getLastName()).isEqualTo(testClient.getLastName());
    }


    @Test
    public void shouldFindClientById() {
        // Given
        ClientDTO newClient = addClient();

        // When
        ResponseEntity<ClientDTO> responseById = restTemplate.getForEntity(LOCALHOST + port + BY_ID + newClient.getId(), ClientDTO.class);

        // Then

        assertThat(responseById.getBody())
                .isNotNull()
                .isEqualTo(newClient);
    }

    private ClientDTO addClient() {
        HttpEntity<ClientDTO> request = new HttpEntity<>(testClient);
        ResponseEntity<ClientDTO> addClientEntity = restTemplate.postForEntity(LOCALHOST + port + UPDATE, request, ClientDTO.class);
        ClientDTO newClient = addClientEntity.getBody();
        assertThat(newClient).isNotNull();
        return newClient;
    }

    private List<ClientDTO> getClientDTOs(ResponseEntity<ClientDTO[]> responseEntity) {
        ClientDTO[] body = responseEntity.getBody();
        return Arrays.asList(body);
    }

}
