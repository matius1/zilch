package com.skocz.mateusz.zilch.service;


import com.skocz.mateusz.zilch.model.Client;
import com.skocz.mateusz.zilch.model.ClientDTO;
import com.skocz.mateusz.zilch.model.ClientMapper;
import com.skocz.mateusz.zilch.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Cacheable
    public List<ClientDTO> findAll() {
        log.info("Find all clients");
        List<Client> allClients = clientRepository.findAll();
        log.info("Returning [{}] records", allClients.size());

        return clientMapper.clientListToClientDtoList(allClients);
    }

    public ClientDTO findById(Long id) {
        log.info("Searching for client by id: [{}]", id);
        Optional<Client> maybeClient = clientRepository.findById(id);
        if(maybeClient.isPresent()){
            Client client = maybeClient.get();
            ClientDTO clientDTO = clientMapper.clientToClientDto(client);
            log.info("Found client: [{}]", clientDTO);
            return clientDTO;
        }else {
            log.info("Client with id=[{}] not found", id);
            //todo:
            return null;
        }
    }


    @Caching(evict = {
            @CacheEvict(value = "clientCache", allEntries = true)
    })
    public ClientDTO saveClient(ClientDTO clientDTO) {
        Long id = clientDTO.getId();
        if (id == null) {
            //create method isValid? / isNew?
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


    public boolean clearDb(){
        clientRepository.deleteAll();
        log.info("All records removed from DB");
        return true;
    }

    @Autowired
    private CacheManager cacheManager;

    public void flushCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName).clear();
            log.info("All records removed from Cache");
        }
    }

}
