## What is it?
This repository contains solution for interview task

## Purpose
#### The purpose of this application is to show how to use cache and database.

##### Application starts with empty cache and db.
##### When you add record it will be added just to db.
Log: `Saving new client: [ClientDTO(id=null, firstName=F Name 1, lastName=L Name 1)]`
##### When you search for all records, they will be cached under empty key `(SimpleKey.EMPTY)`.
Log: `Computed cache key 'SimpleKey []' for operation Builder[public java.util.List com.skocz.mateusz.zilch.service.ClientService.findAll()] caches=[clientCache]`
##### When you search for records by ID, it will be cached. No other records will be cached.
Log: `Computed cache key '4' for operation Builder[public com.skocz.mateusz.zilch.model.ClientDTO com.skocz.mateusz.zilch.service.ClientService.findById(java.lang.Long)] caches=[clientCache]`
## About
- application uses in memory database H2
- application can return all clients or find by id. Its possible to add new records and modify existing ones
- you can test it by:
    - running integration tests: `ClientControllerIntegrationTest` and `ClientServiceIntegrationTest`  
    - running application and sending request to do action: 
        - find all client
            - url: `localhost:8080/client/all`
            - body: `-`
        - add new client
            - url: `localhost:8080/client/update`
            - body: `{
                     	"firstName": "F Name",
                     	"lastName": "L Name"
                     }`
        - edit existing client
            - url: `localhost:8080/client/update`
            - body: `{
                        "id": "1"
                     	"firstName": "F Name",
                     	"lastName": "L Name"
                     }`
         - purge db and cache
            - url: `localhost:8080/client/purge`
            - body: `-`
            
            
 
## How to run it
- Run class: `ZilchApplicaton`
- Application starts on port 8080. You can change it by creating property `server.port` in application.properties` file

## Dependencies
This code is using Lombok. To properly open this code in Intellij you need Lombok plugin. 


#### Author: Mateusz Skocz