package com.skocz.mateusz.zilch.controller;

import com.skocz.mateusz.zilch.model.ClientDTO;
import com.skocz.mateusz.zilch.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping(value = "/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping(value = "/all")
    public ResponseEntity findAll() {
        log.info("Request: findAll");
        return ok(clientService.findAll());
    }

    @GetMapping(value = "/id/{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        log.info("Request: findById=[{}]", id);
        Optional<ClientDTO> maybeClient = clientService.findById(id);
        return maybeClient.isPresent() ? ok(maybeClient.get()) : notFound().build();
    }

    @PostMapping(value = "/update")
    public ResponseEntity update(@RequestBody ClientDTO clientDTO) {
        log.info("Request: update=[{}]", clientDTO);
        Optional<ClientDTO> maybeClient = clientService.saveClient(clientDTO);
        return maybeClient.isPresent() ? ok(maybeClient.get()) : notFound().build();
    }

    @GetMapping(value = "/purge")
    public ResponseEntity purge() {
        log.info("Request: purge");
        clientService.flushCache();
        return ok(clientService.clearDb() ? "DB and Cache cleared" : "DB not cleared");
    }


}
