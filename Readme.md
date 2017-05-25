
Please see below the requirements for the Reservation API.
 
Reservation API:
 
The WarehouseStock Reservation API purpose is to allow customers to reserve stock when they add items to their bag, so that they can continue shopping without losing the stock. A reservation has an expiry time, which can be extended.

When the customer goes to checkout, the stock should still be assigned to them, allowing them to purchase the items.
 
Below is a list of features that the Reservation domain must support. Note that it doesn’t mean each of these features requires a separate API endpoint.
 
Functional requirements

*   Add/update items in a reservation

	 o   Adds items to the reservation, reducing the available stock (provided there is stock available)

     o   If the reservation doesn’t exist, a new one is created

     o   If the reservation already has any of the added items, only the reserved quantity should be updated (expiry time should not change)

     o   If the updated quantity is 0, the item is removed from the reservation.

     o   If there is no available stock for any of the added items, the reservation should not be created.

     o   Cannot reserve more than 10 units per item

     o   Cannot have more than 500 units reserved

*   Remove items from a reservation

     o   Removes the items from a reservation, freeing up available stock

     o   If the reservation has no items left, it should be deleted

*   Extend reservation time

     o   Extends the expiry time of all the reserved items

*   Delete a reservation

     o   Removes all items from a reservation, freeing up available stock.

*   Expire a reservation

     o   Expires a reserved item when it’s expiry time is due, freeing up available stock

     o   If the reservation has no items left, it should be deleted

*   Notify other systems of stock changes

     o   Whenever the available stock changes for a given item, a message should be published containing the current available stock, SKU and Warehouse

     o   If an item fails to be reserved (due to no available stock), then a message should be published containing the SKU, Store, and current available stock per associated warehouse

 
Non-functional requirements
*   Reservations should be persisted

*   Concurrent requests trying to reserve the same item should both succeed, given that there is available stock for both.

*   Available quantity (as defined below) should not go below 0, given that InStock has not decreased .

 
 ==================================================
 
Considerations
*   Reservations are placed using an item’s VariantId, which is a public facing identifier for a given item.

*   Reservations are placed against a store (eg: COM), which has one or more associated warehouses (eg: FC01, ??). The warehouses have an InStock quantity for an item.

*   Querying available stock has to be done using an item’s SKU, so items have to be looked up first using the VariantId. An item has a VariantId (product identifier for an item) and a SKU (warehouse identifier for an item) and is associated with one or more warehouses, as defined   by the store/warehouse mapping above.

*   An item’s available quantity is calculated as InStock – ReservedQuantity – AllocatedQuantity. For the purposes of this exercise, AllocatedQuantity is 0.

---
 
The current contracts can be found at: 

https://asos203-test-inv-warehousestock.asosdevelopment.com/swagger/ui/index#!/Reservation
 
=============================================

Feature: CreateReservationFeature

Background: Scenario setup
Given current time is "2000-01-01 00:00:00"
And the following Variants map to according Skus
| VariantId | Sku  |
| 1         | Sku1 |
| 2         | Sku2 |
| 3         | Sku3 |
And I have this available stock
| Sku    | AvailableQuantity |
| Sku1   | 20                |
| Sku2   | 3                 |
| Sku3   | 0                 |


Scenario: Reserve items
When I try to reserve this bag for store "COM"
| VariantId | Quantity | ExpiryTimeInMinutes |
| 1         | 10       | 90           |
| 2         | 5        | 45           |
| 3         | 2        | 90           |
Then the reservation with store "COM" is persisted the repository
| Sku  | QuantityReserved | ExpiryDateTime      |
| Sku1 | 10               | 2000-01-01 01:30:00 |
| Sku2 | 3                | 2000-01-01 00:45:00 |
And the response should be
| VariantId | QuantityReserved | ExpiryDateTime      |
| 1         | 10               | 2000-01-01 01:30:00 |
| 2         | 3                | 2000-01-01 00:45:00 |
| 3         | 0                | 2000-01-01 01:30:00 |
And the location on the response should be "reservation/" plus reservationID

