package com.servicein.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.servicein.data.repository.ChatRepository
import com.servicein.domain.model.Chat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatRepositoryTest {

    private lateinit var repository: ChatRepository
    private val firestore: FirebaseFirestore = mockk(relaxed = true)
    private val chatCollection: CollectionReference = mockk(relaxed = true)
    private val documentReference: DocumentReference = mockk(relaxed = true)
    private val documentSnapshot: DocumentSnapshot = mockk(relaxed = true)
    private val querySnapshot: QuerySnapshot = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { firestore.collection("chats") } returns chatCollection
        every { chatCollection.document(any()) } returns documentReference
        every { chatCollection.document() } returns documentReference

        repository = ChatRepository(firestore)
    }

    @Test
    fun `createOrGetChat returns existing chat id when chat exists`() = runTest {
        val mockQuery = mockk<Query>()
        val docId = "existingChatId"

        every { chatCollection.whereEqualTo("shopId", "shop1") } returns mockQuery
        every { mockQuery.whereEqualTo("customerId", "cust1") } returns mockQuery

        coEvery { mockQuery.get() } returns Tasks.forResult(querySnapshot)
        every { querySnapshot.documents } returns listOf(documentSnapshot)
        every { documentSnapshot.id } returns docId

        val result = repository.createOrGetChat("shop1", "cust1", "ShopName", "CustName")

        assertTrue(result.isSuccess)
        assertEquals(docId, result.getOrNull())
    }

    @Test
    fun `createOrGetChat creates new chat when none exists`() = runTest {
        val mockQuery = mockk<Query>()
        val newChatRef = mockk<DocumentReference>()
        val newDocId = "newChatId"

        every { chatCollection.whereEqualTo("shopId", "shop1") } returns mockQuery
        every { mockQuery.whereEqualTo("customerId", "cust1") } returns mockQuery
        coEvery { mockQuery.get() } returns Tasks.forResult(querySnapshot)
        every { querySnapshot.documents } returns emptyList()

        coEvery { chatCollection.add(any()) } returns Tasks.forResult(newChatRef)
        every { newChatRef.id } returns newDocId

        val result = repository.createOrGetChat("shop1", "cust1", "ShopName", "CustName")

        assertTrue(result.isSuccess)
        assertEquals(newDocId, result.getOrNull())
    }

    @Test
    fun `sendMessage updates chat with new message`() = runTest {
        val chatId = "chat123"
        val chatRef = mockk<DocumentReference>()
        val existingChat = Chat(
            id = chatId,
            customerId = "cust1",
            customerName = "Cust",
            shopId = "shop1",
            shopName = "Shop",
            messages = emptyList()
        )

        every { chatCollection.document(chatId) } returns chatRef
        every { chatRef.id } returns chatId

        coEvery { chatRef.get() } returns Tasks.forResult(documentSnapshot)

        every { documentSnapshot.toObject(Chat::class.java) } returns existingChat
        coEvery { chatRef.set(any()) } returns Tasks.forResult(null)

        val result = repository.sendMessage(chatId, "Hello")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteChatByCustomerAndShop deletes existing chat`() = runTest {
        val mockQuery = mockk<Query>()
        every { chatCollection.whereEqualTo("customerId", "cust1") } returns mockQuery
        every { mockQuery.whereEqualTo("shopId", "shop1") } returns mockQuery
        coEvery { mockQuery.get() } returns Tasks.forResult(querySnapshot)
        every { querySnapshot.documents } returns listOf(documentSnapshot)
        every { documentSnapshot.reference } returns documentReference
        coEvery { documentReference.delete() } returns Tasks.forResult(null)

        val result = repository.deleteChatByCustomerAndShop("cust1", "shop1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteChatByCustomerAndShop returns failure when chat not found`() = runTest {
        val mockQuery = mockk<Query>()
        every { chatCollection.whereEqualTo("customerId", "cust1") } returns mockQuery
        every { mockQuery.whereEqualTo("shopId", "shop1") } returns mockQuery
        coEvery { mockQuery.get() } returns Tasks.forResult(querySnapshot)
        every { querySnapshot.documents } returns emptyList()

        val result = repository.deleteChatByCustomerAndShop("cust1", "shop1")

        assertTrue(result.isFailure)
        assertEquals("Chat not found", result.exceptionOrNull()?.message)
    }
}
