package com.skocz.mateusz.zilch.service;

import com.skocz.mateusz.zilch.model.Client;
import com.skocz.mateusz.zilch.model.ClientDTO;
import com.skocz.mateusz.zilch.model.ClientMapper;
import com.skocz.mateusz.zilch.repository.ClientRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repoMock;
    @Mock
    private CacheManager cacheManagerMock;

    private ClientMapper clientMapper = new ClientMapper();

    private ClientService service;


    public static final Client CLIENT_1 = Client.builder()
            .id(1L)
            .firstName("F Name 1")
            .lastName("L Name 1")
            .build();

    public static final Client CLIENT_2 = Client.builder()
            .id(2L)
            .firstName("F Name 2")
            .lastName("L Name 2")
            .build();

    public static final ClientDTO CLIENTDTO_TO_SAVE = ClientDTO.builder()
            .firstName("F Name 2")
            .lastName("L Name 2")
            .build();

    @Test
    public void shouldReturnAll() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        List<Client> givenAllClients = Lists.list(CLIENT_1, CLIENT_2);
        when(repoMock.findAll()).thenReturn(givenAllClients);

        // When
        List<ClientDTO> result = service.findAll();

        // Then
        assertThat(result).hasSize(givenAllClients.size());
    }

    @Test
    public void shouldReturnEmptyList_whenNoRecords() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);

        // When
        List<ClientDTO> result = service.findAll();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldFindById() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        when(repoMock.findById(CLIENT_1.getId())).thenReturn(of(CLIENT_1));

        // When
        Optional<ClientDTO> result = service.findById(CLIENT_1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(clientMapper.clientToClientDto(CLIENT_1));
    }

    @Test
    public void shouldNotFindById() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        when(repoMock.findById(CLIENT_1.getId())).thenReturn(Optional.empty());

        // When
        Optional<ClientDTO> result = service.findById(CLIENT_1.getId());

        // Then
        assertThat(result).isNotPresent();
    }

    @Test
    public void shouldSaveNewClient() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        Client savedClient = clientMapper.clientDtoToClient(CLIENTDTO_TO_SAVE);
        savedClient.setId(99L);
        when(repoMock.save(any())).thenReturn(savedClient);

        // When
        Optional<ClientDTO> result = service.saveClient(CLIENTDTO_TO_SAVE);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(clientMapper.clientToClientDto(savedClient));
    }

    @Test
    public void shouldUpdateExistingClient() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        when(repoMock.findById(CLIENT_1.getId())).thenReturn(Optional.of(CLIENT_1));
        when(repoMock.save(any())).thenReturn(CLIENT_1);

        // When
        Optional<ClientDTO> result = service.saveClient(clientMapper.clientToClientDto(CLIENT_1));

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(clientMapper.clientToClientDto(CLIENT_1));
    }

    @Test
    public void shouldUpdateNotExistingClient() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        when(repoMock.findById(CLIENT_1.getId())).thenReturn(Optional.empty());
        when(repoMock.save(any())).thenReturn(CLIENT_1);

        // When
        Optional<ClientDTO> result = service.saveClient(clientMapper.clientToClientDto(CLIENT_1));

        // Then
        assertThat(result).isNotPresent();
    }

    @Test
    public void shouldClearDb() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);

        // When
        boolean result = service.clearDb();

        // Then
        assertThat(result).isTrue();
        verify(repoMock).deleteAll();
    }

    @Test
    public void shouldFlushCache() {
        // Given
        service = new ClientService(repoMock, clientMapper, cacheManagerMock);
        String cacheName = "cache1";
        when(cacheManagerMock.getCacheNames()).thenReturn(Lists.list(cacheName));
        when(cacheManagerMock.getCache(cacheName)).thenReturn(mock(Cache.class));

        // When
        service.flushCache();

        // Then
        verify(cacheManagerMock.getCache(cacheName)).clear();
    }

}