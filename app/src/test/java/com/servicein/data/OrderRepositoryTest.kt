package com.servicein.data

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.servicein.core.util.OrderStatus
import com.servicein.core.util.OrderType
import com.servicein.data.repository.OrderRepository
import com.servicein.domain.model.Order
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class OrderRepositoryTest {

    private val firestore: FirebaseFirestore = mockk(relaxed = true)
    private val ordersCollection: CollectionReference = mockk(relaxed = true)
    private val documentReference: DocumentReference = mockk(relaxed = true)
    private val documentSnapshot: DocumentSnapshot = mockk(relaxed = true)
    private val listenerRegistration: ListenerRegistration = mockk(relaxed = true)
    private val querySnapshot: QuerySnapshot = mockk(relaxed = true)

    private lateinit var repository: OrderRepository

    @Before
    fun setUp() {
        every { firestore.collection("orders") } returns ordersCollection
        every { ordersCollection.document(any()) } returns documentReference
        every { ordersCollection.document() } returns documentReference

        repository = OrderRepository(firestore)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `listenToOrderById returns order when document exists`() {
        val orderId = "order123"
        val order = Order(id = orderId, customerName = "Test Customer")

        val eventListenerSlot = slot<EventListener<DocumentSnapshot>>()
        every { ordersCollection.document(orderId).addSnapshotListener(capture(eventListenerSlot)) } returns listenerRegistration
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.toObject(Order::class.java) } returns order
        every { documentSnapshot.id } returns orderId

        var result: Result<Order?>? = null
        repository.listenToOrderById(orderId) { result = it }

        eventListenerSlot.captured.onEvent(documentSnapshot, null)

        assertNotNull(result)
        assertTrue(result!!.isSuccess)
        assertEquals(order, result!!.getOrNull())
    }

    @Test
    fun `getOrdersByCustomerIdAndStatus returns correct list`() = runTest {
        val order1 = Order(id = "1", customerName = "A", orderStatus = "RECEIVED")
        val order2 = Order(id = "2", customerName = "B", orderStatus = "RECEIVED")

        val query: Query = mockk(relaxed = true)
        val doc1: DocumentSnapshot = mockk {
            every { toObject(Order::class.java) } returns order1
            every { id } returns "1"
        }
        val doc2: DocumentSnapshot = mockk {
            every { toObject(Order::class.java) } returns order2
            every { id } returns "2"
        }

        val querySnapshot: QuerySnapshot = mockk {
            every { documents } returns listOf(doc1, doc2)
        }

        every { ordersCollection.whereEqualTo("customerId", "cust123") } returns query
        every { query.whereIn("orderStatus", listOf("RECEIVED")) } returns query
        every { query.orderBy("dateTime", Query.Direction.DESCENDING) } returns query
        coEvery { query.get() } returns Tasks.forResult(querySnapshot)

        val result = repository.getOrdersByCustomerIdAndStatus("cust123", listOf(OrderStatus.RECEIVED))

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `completeOrder updates order with rating and review`() = runTest {
        every { ordersCollection.document("orderId") } returns documentReference
        coEvery {
            documentReference.update(
                mapOf(
                    "orderStatus" to OrderStatus.COMPLETED.name,
                    "rating" to 5,
                    "review" to "Great service"
                )
            )
        } returns Tasks.forResult(null)

        val result = repository.completeOrder("orderId", 5, "Great service")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `createOrder successfully creates order`() = runTest {
        val order = Order(
            id = "generatedId",
            customerName = "John",
            customerId = "cust123",
            shopId = "shop123",
            shopName = "Shop Name",
            orderStatus = OrderStatus.RECEIVED.name,
            orderType = OrderType.EMERGENCY_SERVICE.name,
            latitude = -6.2,
            longitude = 106.8,
            dateTime = "2025-06-26T12:00:00Z",
            value = 20000
        )

        every { ordersCollection.document() } returns documentReference
        every { documentReference.id } returns order.id
        coEvery { documentReference.set(any()) } returns Tasks.forResult(null)

        val result = repository.createOrder(
            customerName = order.customerName,
            customerId = order.customerId,
            shopId = order.shopId,
            shopName = order.shopName,
            orderType = OrderType.valueOf(order.orderType),
            latitude = order.latitude,
            longitude = order.longitude,
            dateTime = order.dateTime,
            value = order.value
        )

        assertTrue(result.isSuccess)
    }
}