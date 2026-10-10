package com.nexus.common.events;

// skuId is the opaque product/SKU identifier to reserve stock for. Nested inside
// InventoryReservationRequestedEvent -- not a DomainEvent itself.
public record ReservationLineItem(String skuId, int quantity) {
}
