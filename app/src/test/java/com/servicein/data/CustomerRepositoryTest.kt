package com.servicein.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.servicein.data.repository.CustomerRepository
import com.servicein.domain.model.Customer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CustomerRepositoryTest {

    private lateinit var repository: CustomerRepository
    private val firestore: FirebaseFirestore = mockk(relaxed = true)
    private val customerCollection: CollectionReference = mockk(relaxed = true)
    private val documentReference: DocumentReference = mockk(relaxed = true)
    private val documentSnapshot: DocumentSnapshot = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { firestore.collection("customers") } returns customerCollection
        every { customerCollection.document(any()) } returns documentReference
        every { documentReference.id } returns "cust123"

        repository = CustomerRepository(firestore)
    }

    @Test
    fun `createCustomer creates new customer when not exists`() = runTest {
        coEvery { documentReference.get() } returns Tasks.forResult(documentSnapshot)
        every { documentSnapshot.exists() } returns false

        coEvery { documentReference.set(any()) } returns Tasks.forResult(null)

        val result = repository.createCustomer("cust123", "John Doe")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `createCustomer updates existing customer with name fallback`() = runTest {
        val existingCustomer = Customer("cust123", "Jane Doe", 5000)

        coEvery { documentReference.get() } returns Tasks.forResult(documentSnapshot)
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.toObject(Customer::class.java) } returns existingCustomer

        coEvery { documentReference.set(any()) } returns Tasks.forResult(null)

        val result = repository.createCustomer("cust123", "")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getCustomerById returns customer when exists`() = runTest {
        val customer = Customer("cust123", "John", 2000)

        coEvery { documentReference.get() } returns Tasks.forResult(documentSnapshot)
        every { documentSnapshot.exists() } returns true
        every { documentSnapshot.toObject(Customer::class.java) } returns customer
        every { documentSnapshot.id } returns "cust123"

        val result = repository.getCustomerById("cust123")

        assertTrue(result.isSuccess)
        assertEquals(customer.copy(id = "cust123"), result.getOrNull())
    }

    @Test
    fun `getCustomerById returns null when not exists`() = runTest {
        coEvery { documentReference.get() } returns Tasks.forResult(documentSnapshot)
        every { documentSnapshot.exists() } returns false

        val result = repository.getCustomerById("cust123")

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `addToWallet adds amount to existing customer`() = runTest {
        val expectedNewBalance = 2500

        coEvery {
            firestore.runTransaction<Int>(any())
        } returns Tasks.forResult(expectedNewBalance)

        val result = repository.addToWallet("cust123", 500)

        assertTrue(result.isSuccess)
        assertEquals(expectedNewBalance, result.getOrNull())
    }

    @Test
    fun `deductFromWallet deducts amount when balance sufficient`() = runTest {
        val newBalance = 2000

        coEvery {
            firestore.runTransaction<Int>(any())
        } returns Tasks.forResult(newBalance)

        val result = repository.deductFromWallet("cust123", 1000)

        assertTrue(result.isSuccess)
        assertEquals(newBalance, result.getOrNull())
    }

    @Test
    fun `deductFromWallet fails when balance insufficient`() = runTest {
        coEvery {
            firestore.runTransaction<Int>(any())
        } returns Tasks.forException(Exception("Insufficient balance"))

        val result = repository.deductFromWallet("cust123", 1000)

        assertTrue(result.isFailure)
        assertEquals("Insufficient balance", result.exceptionOrNull()?.message)
    }
}
