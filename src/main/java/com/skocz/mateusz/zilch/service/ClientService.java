package com.skocz.mateusz.zilch.service;


import com.skocz.mateusz.zilch.model.Client;
import com.skocz.mateusz.zilch.model.ClientDTO;
import com.skocz.mateusz.zilch.model.ClientMapper;
import com.skocz.mateusz.zilch.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = {"clientCache"})
public class ClientService {

    @Autowired
    private final ClientRepository clientRepository;

    @Autowired
    private final ClientMapper clientMapper;

    @Autowired
    private final CacheManager cacheManager;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, CacheManager cacheManager) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.cacheManager = cacheManager;
    }

    @Cacheable
    public List<ClientDTO> findAll() {
        List<Client> allClients = clientRepository.findAll();
        log.info("Found [{}] records", allClients.size());
        return clientMapper.clientListToClientDtoList(allClients);
    }

    @Cacheable
    public ClientDTO findById(Long id) {
        log.info("Searching client by id: [{}]", id);
        Optional<Client> maybeClient = clientRepository.findById(id);
        if (maybeClient.isPresent()) {
            Client client = maybeClient.get();
            ClientDTO clientDTO = clientMapper.clientToClientDto(client);
            log.info("Found client: [{}]", clientDTO);
            return clientDTO;
        } else {
            log.info("Client with id=[{}] not found", id);
            return null;
        }
    }


    @Caching(evict = {@CacheEvict(value = "clientCache", allEntries = true)})
    public ClientDTO saveClient(ClientDTO clientDTO) {
        Long id = clientDTO.getId();
        if (id == null) {
            log.info("Saving new client: [{}]", clientDTO);
            Client clientToSave = clientMapper.clientDtoToClient(clientDTO);
            Client savedClient = clientRepository.save(clientToSave);
            return clientMapper.clientToClientDto(savedClient);
        } else {
            log.info("Updating existing client: [{}]", clientDTO);
            if (clientRepository.findById(id).isPresent()) {
                Client clientToSave = clientMapper.clientDtoToClient(clientDTO);
                Client savedClient = clientRepository.save(clientToSave);
                return clientMapper.clientToClientDto(savedClient);
            } else {
                log.info("Failed to update existing client: [{}]", clientDTO);
                //TODO: FIX IT!
                return null;
            }
        }

    }

    public boolean clearDb() {
        clientRepository.deleteAll();
        log.info("All records removed from DB");
        return true;
    }

    public void flushCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("All records removed from Cache [{}]", cacheName);
            }
        }
    }

}
