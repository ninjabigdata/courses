# OrderMyFood (OMF)
## _The Food Delivery App_

## Running the Application

- ## Start the zipkin.jar (_Since size of zipkin.jar is more than 50MB, it was not added in the the solution directory_)
    - Download the zipkin application from https://zipkin.io/quickstart.sh
    - Run the downloaded sh file. This will download the zipkin.jar
    - Start the Zipkin server
        ```sh
        cd <download-location>
        java -jar zipkin.jar
        ```
- ## Start the microservices
    - service-discovery
    - gateway-service
    - order-management-service
    - restaurant-search-service

## _Use Cases_
- Search Restaurants - by using one or multiple criteria. Case-insensitive search (URL: POST http://localhost:8900/restaurant-search/search)
  - name
  - distance
  - cuisine
    - SOUTH_INDIAN
    - MULTI_CUISINE
    - NORTH_INDIAN
    - ITALIAN
    - CHINESE
  - budget
  - location
    _Sample Input_
    ```
    {
        "name": "briyani",
        "distance": 5,
        "cuisine": "SOUTH_INDIAN",
        "budget": 450,
        "location": "sector"
    }
    ```
- View Menu By Restaurant Id (URL: GET http://localhost:8900/restaurant-search/{id}/menu)
- Order Food (URL: POST http://localhost:8900/order-management/order)
  _Sample Input_
    ```
    {
        "restaurantId": 2,
        "customer": {
            "name": "balaji",
            "address": "address",
            "mobileNumber": "9791941815"
        },
        "items": [
            {
                "itemId": 10,
                "quantity": 1
            }
        ],
        "payment": {
            "amount": 229.0
        }
    }
    ```
- View Order (URL: GET http://localhost:8900/order-management/order/{orderId})
- Update Order (URL: PUT http://localhost:8900/order-management/order/{orderId}/{mobile-number})
  - Only order in Accepted Status can be updated
  - Only mobile number can be updated
- Cancel Order (URL: GET http://localhost:8900/order-management/order/{orderId}/cancel)
  - Only order in Accepted Status can be cancelled