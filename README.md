# Sentifi Stock Price Service

There are few ways to try the application 

## Using Git & Java 

This assumes you have `git` & `java` installed locally **_and_** some time 

- go to your working directory 
- clone the repository 
```bash
git clone git@github.com:kayoubi/sentifi.git
cd sentifi
```
- build and start the application 

```bash
./mvnw clean install
java -jar ./target/stockPriceService.jar
```
- the application starts at port 8080, see below for API endpoints 

## Using Docker
- this assumes you have `docker` installed locally, I have an image `kayoubi/sentifi` in DockerHub that you can run locally as follow
```bash
docker run -p 8080:8080 kayoubi/sentifi
```  

## Just test Remotely (AWS)
I deployed the application into AWS EC2
```
http://ec2-54-191-154-177.us-west-2.compute.amazonaws.com:8080
```
Just append the URL of any of the API below e.g.
```
http://ec2-54-191-154-177.us-west-2.compute.amazonaws.com:8080/api/v2/FB/200dma?startDate=2012-12-10
```


# REST API
**request a Close Price for a ticker symbol for a range of dates**
---
- **URL**

    `/api/v2/{symbol}/closePrice?startDate={start-date}&endDate={end-date}`
- **Method**

    `GET`
- **Path Variable**
  - *symbol* must be a valid quandl symbol
   
- **Request Params**

  - *start-date* is mandatory, valid format "yyyy-MM-dd"
  - *end-date* is mandatory, valid format "yyyy-MM-dd"

- **Success Response:**

  - **Code:** 200 OK
  - **Content:** 
  
  ```json
    { "Prices":
      [
        {
          "Ticker":"GE",
          "DateClose":[
            ["2017-12-01","17.88"],
            ["2017-11-30","18.29"],
            ["2017-11-29","18.48"]
          ]
        }
      ]
    }
  ```
 
- **Failed Response:**
  - For invalid date format / missing date params / invalid quandl symbol
  - **Code:** 404 NOT_FOUND
  - **Message** A detailed message about the error  
  
  
  
  **request the 200 day moving average price for a ticker symbol beginning with a start date**
  ---
  - **URL**
  
      `/api/v2/{symbol}/200dma?startDate={start-date}`
  - **Method**
  
      `GET`
  - **Path Variable**
    - *symbol* must be a valid quandl symbol
     
  - **Request Params**
  
    - *start-date* is mandatory, valid format "yyyy-MM-dd"
  
  - **Success Response:**
  
    - **Code:** 200 OK
    - **Content:** 
    
    ```json
      {
        "200dma": {
          "Ticker":"FB",
          "Avg":"0.0",
          "OldestAvailableDate":"2012-05-18"
        }
      }
    ```
    `OldestAvailableDate` is optional and will show only if the `Avg` is 0
   
  - **Failed Response:**
    - For invalid date format / missing date params / invalid quandl symbol
    - **Code:** 404 NOT_FOUND
    - **Message** A detailed message about the error 
    
    
  **request for the 200 day moving average price for a up to 1000 ticker symbols beginning with a start date**
  ---
  - **URL**
  
      `/api/v2/200dma/{symbols}?startDate={start-date}`
  - **Method**
  
      `GET`
  - **Path Variable**
    - *symbols* a common separated list of valid quandl symbols (e.g. `/200dma/FB,GE,..?startDate=...`)
     
  - **Request Params**
  
    - *start-date* is mandatory, valid format "yyyy-MM-dd"
  
  - **Success Response:**
  
    - **Code:** 200 OK
    - **Content:** 
    
    ```json
    [
      {
        "200dma":{
          "Ticker":"GE",
          "Avg": "16.69336956521739"
        }
      },
      {
        "200dma":{
          "Ticker":"FB",
          "Avg":"0.0",
          "OldestAvailableDate":"2012-05-18"}
      }
    ]      
    ```
    `OldestAvailableDate` is optional and will show only if the `Avg` is 0
   
  - **Failed Response:**
    - For invalid date format / missing date params / invalid quandl symbol
    - **Code:** 404 NOT_FOUND
    - **Message** A detailed message about the error
    
    
# Parallelism and Caching
* when requesting multiple symbols in the same request, parallel calls are done simultaneously to improve performance, this is done using default java 8 parallel stream, however if more fine-tuning required, other mechanism like Join/Fork and thread pool could be utilized
* cashing is done as following:
    - check if the key exist in the cash, if not query the API and put the result in the cache
    - if the key exists, we have few scenarios (each implemented using a subclass of `CacheExtractorStrategy`) 
        - the requested date range already cached: we just filter the cache
        - the requested date range overlaps with the cache: we just query the missing data and append to the cache
        - the requested date range is larger than the cache (on both sides), we query the new date range and replace the cache
        - the requested date range is completely before / after the cached date, we query the new set and replace the cache        
    - cache internally uses `LinkedHashSet` to support `FIFO` with limit size to `10000`
          