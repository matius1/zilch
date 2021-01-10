package com.skocz.mateusz.zilch.controller;

import com.skocz.mateusz.zilch.model.ClientDTO;
import com.skocz.mateusz.zilch.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping(value = "/all")
    public List<ClientDTO> findAll() {
        log.info("Request: findAll");
        return clientService.findAll();
    }

    @GetMapping(value = "/id/{id}")
    public ClientDTO findById(@PathVariable Long id) {
        //todo: what if not found
        return clientService.findById(id);
    }

    @PostMapping(value = "/update")
    public ClientDTO update(@RequestBody ClientDTO clientDTO) {
        return clientService.saveClient(clientDTO);
    }

    @GetMapping(value = "/clearDb")
    public String clearDb() {
        //todo: return response codes?
        return clientService.clearDb() ? "DB cleared" : "Db not cleared";
    }
    
    @PostMapping(value = "flushCache")
    public void flushCache() {
        clientService.flushCache();
    }

}
