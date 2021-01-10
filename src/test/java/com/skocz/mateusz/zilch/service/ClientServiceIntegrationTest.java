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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ZilchApplication.class)
@ExtendWith(SpringExtension.class)
public class ClientServiceIntegrationTest {
    public static final String CACHE_NAME = "clientCache";

    //todo: move to integration package

    @Autowired
    CacheManager cacheManager;

    @Autowired
    ClientService clientService;
    public static final ClientDTO CLIENT_1 = ClientDTO.builder()
            .id(1L)
            .firstName("Mat")
            .lastName("Skocz")
            .build();

    @BeforeEach
    public void setUp() {
        List<ClientDTO> all = clientService.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    public void shouldPopulateCache_whenClientAdded_afterReadingFromService() {
        // When
        clientService.saveClient(CLIENT_1);

        //check cache before read -> should be empty
        int clientCacheSizeBeforeFill = getClientCacheSize();
        assertThat(clientCacheSizeBeforeFill).isEqualTo(0);

        //read from service
        List<ClientDTO> allAfterSave = clientService.findAll();
//        assertThat(allAfterSave).hasSize(1);

        //check cache before read -> should be filled
        int clientCacheSizeAfterFill = getClientCacheSize();
        assertThat(clientCacheSizeAfterFill).isEqualTo(1);
    }

    public int getClientCacheSize() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        Object nativeCache = cache.getNativeCache();
        if (nativeCache instanceof Map) {
            Map cacheMap = (Map) nativeCache;
            return cacheMap.size();
        }
        return -1;

    }

}