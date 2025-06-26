package com.servicein.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.servicein.data.repository.ShopRepository
import com.servicein.domain.model.Shop
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ShopRepositoryTest {

    private lateinit var repository: ShopRepository

    private val firestore: FirebaseFirestore = mockk(relaxed = true)
    private val shopCollection: CollectionReference = mockk(relaxed = true)
    private val documentReference: DocumentReference = mockk(relaxed = true)
    private val documentSnapshot: DocumentSnapshot = mockk(relaxed = true)
    private val querySnapshot: QuerySnapshot = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { firestore.collection("shops") } returns shopCollection
        every { shopCollection.document(any()) } returns documentReference
        repository = spyk(ShopRepository(firestore))
    }

    // --- getAllShops ---

    @Test
    fun `getAllShops returns list of shops`() = runTest {
        val shop1 = Shop(id = "shop1", shopName = "Shop A", wallet = 1000)
        val shop2 = Shop(id = "shop2", shopName = "Shop B", wallet = 2000)

        val mockDoc1 = mockk<QueryDocumentSnapshot>()
        val mockDoc2 = mockk<QueryDocumentSnapshot>()

        coEvery { shopCollection.get() } returns Tasks.forResult(querySnapshot)

        every { querySnapshot.iterator() } returns listOf(mockDoc1, mockDoc2).toMutableList().iterator()
        every { mockDoc1.toObject(Shop::class.java) } returns shop1
        every { mockDoc2.toObject(Shop::class.java) } returns shop2

        val result = repository.getAllShops()

        assertTrue(result.isSuccess)
        assertEquals(listOf(shop1, shop2), result.getOrNull())
    }

    @Test
    fun `getAllShops returns failure on exception`() = runTest {
        coEvery { shopCollection.get() } throws RuntimeException("Firestore error")

        val result = repository.getAllShops()

        assertTrue(result.isFailure)
        assertEquals("Firestore error", result.exceptionOrNull()?.message)
    }

    // --- getShopById ---

    @Test
    fun `getShopById returns shop when exists`() = runTest {
        val shop = Shop(id = "shop1", shopName = "Shop A", wallet = 1500)

        coEvery { documentReference.get() } returns Tasks.forResult(documentSnapshot)
        every { documentSnapshot.toObject(Shop::class.java) } returns shop

        val result = repository.getShopById("shop1")

        assertTrue(result.isSuccess)
        assertEquals(shop, result.getOrNull())
    }

    @Test
    fun `getShopById returns failure on exception`() = runTest {
        coEvery { documentReference.get() } throws RuntimeException("Failed to fetch")

        val result = repository.getShopById("shop1")

        assertTrue(result.isFailure)
        assertEquals("Failed to fetch", result.exceptionOrNull()?.message)
    }

    // --- addToWallet ---

    @Test
    fun `addToWallet returns failure when shop not found`() = runTest {
        coEvery { documentReference.get() } returns Tasks.forResult(documentSnapshot)
        every { documentSnapshot.toObject(Shop::class.java) } returns null

        val result = repository.addToWallet("shop1", 500)

        assertTrue(result.isFailure)
        assertEquals("Shop not found", result.exceptionOrNull()?.message)
    }
}
