package com.skocz.mateusz.zilch.service;

import com.skocz.mateusz.zilch.ZilchApplication;
import com.skocz.mateusz.zilch.model.ClientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ZilchApplication.class)
@ExtendWith(SpringExtension.class)
public class ClientServiceIntegrationTest {
    public static final String CACHE_NAME = "clientCache";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ClientService clientService;

    public static final ClientDTO CLIENT_1 = ClientDTO.builder()
            .firstName("F Name 1")
            .lastName("L Name 1")
            .build();

    public static final ClientDTO CLIENT_2 = ClientDTO.builder()
            .firstName("F Name 2")
            .lastName("L Name 2")
            .build();

    @BeforeEach
    public void setUp() {
        clientService.clearDb();
        clientService.flushCache();

        List<ClientDTO> all = clientService.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    public void shouldPopulateCacheFromDB_withAllData_afterFindAll() {
        // Given
        clientService.saveClient(CLIENT_1);
        clientService.saveClient(CLIENT_2);

        Optional<List<ClientDTO>> clientsInCacheBeforeFill = findListOfClientsInCacheByKey(SimpleKey.EMPTY);
        assertThat(clientsInCacheBeforeFill).isEmpty();

        // When
        List<ClientDTO> allAfterSave = clientService.findAll();
        assertThat(allAfterSave).hasSize(2);

        //Then
        Optional<List<ClientDTO>> clientsInCacheAfterFill = findListOfClientsInCacheByKey(SimpleKey.EMPTY);
        assertThat(clientsInCacheAfterFill).isPresent();
        assertThat(clientsInCacheAfterFill.get()).hasSize(2);
    }

    @Test
    public void shouldPopulateCacheFromDB_withFoundData_afterFindByID() {
        // Given
        ClientDTO savedClient1 = clientService.saveClient(CLIENT_1);
        ClientDTO savedClient2 = clientService.saveClient(CLIENT_2);

        Optional<ClientDTO> clientInCache1 = findClientInCacheByKey(savedClient1.getId());
        assertThat(clientInCache1).isNotPresent();

        Optional<ClientDTO> clientInCache2 = findClientInCacheByKey(savedClient2.getId());
        assertThat(clientInCache2).isNotPresent();

        // When
        clientService.findById(savedClient2.getId());

        //Then
        clientInCache1 = findClientInCacheByKey(savedClient1.getId());
        assertThat(clientInCache1).isNotPresent();

        clientInCache2 = findClientInCacheByKey(savedClient2.getId());
        assertThat(clientInCache2).isPresent();
    }

    public Optional<ClientDTO> findClientInCacheByKey(Object key) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        return Optional.ofNullable(cache.get(key, ClientDTO.class));
    }

    public Optional<List<ClientDTO>> findListOfClientsInCacheByKey(Object key) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        return Optional.ofNullable(cache.get(key, List.class));
    }

}